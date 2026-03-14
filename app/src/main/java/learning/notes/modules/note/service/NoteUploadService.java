package learning.notes.modules.note.service;

import learning.notes.common.exception.BusinessException;
import learning.notes.common.exception.ErrorCode;
import learning.notes.common.model.AsyncTaskStatus;
import learning.notes.modules.note.listener.NoteAnalysisProducer;
import learning.notes.modules.note.model.NoteEntity;
import learning.notes.modules.note.repository.NoteAnalysisRepository;
import learning.notes.modules.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;

/**
 * 笔记上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteUploadService {

    private final NoteRepository noteRepository;
    private final NoteAnalysisRepository analysisRepository;
    private final NoteAnalysisProducer analysisProducer;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 上传并分析笔记
     */
    @Transactional
    public Map<String, Object> uploadAndAnalyze(MultipartFile file) {
        validateFile(file);
        
        String fileName = file.getOriginalFilename();
        log.info("收到笔记上传请求: {}, 大小: {} bytes", fileName, file.getSize());

        String fileHash = calculateHash(file);
        Optional<NoteEntity> existingNote = noteRepository.findByFileHash(fileHash);
        
        if (existingNote.isPresent()) {
            return handleDuplicateNote(existingNote.get());
        }

        NoteEntity note = new NoteEntity();
        note.setFileHash(fileHash);
        note.setOriginalFilename(fileName);
        note.setFileSize(file.getSize());
        note.setContentType(file.getContentType());
        note.setAnalyzeStatus(AsyncTaskStatus.PENDING);
        
        NoteEntity savedNote = noteRepository.save(note);

        String content = extractContent(file);
        analysisProducer.sendAnalysisTask(savedNote.getId(), content);

        log.info("笔记上传完成，分析任务已入队: noteId={}", savedNote.getId());

        return Map.of(
            "note", Map.of(
                "id", savedNote.getId(),
                "filename", savedNote.getOriginalFilename(),
                "analyzeStatus", AsyncTaskStatus.PENDING.name()
            ),
            "duplicate", false
        );
    }

    /**
     * 重新分析笔记
     */
    @Transactional
    public void reanalyze(Long id) {
        NoteEntity note = noteRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTE_NOT_FOUND, "笔记不存在"));

        log.info("开始重新分析笔记: noteId={}", id);

        note.setAnalyzeStatus(AsyncTaskStatus.PENDING);
        note.setAnalyzeError(null);
        noteRepository.save(note);

        String content = note.getNoteText() != null ? note.getNoteText() : "";
        analysisProducer.sendAnalysisTask(id, content);

        log.info("重新分析任务已发送: noteId={}", id);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "文件大小不能超过50MB");
        }
    }

    private String calculateHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件哈希计算失败");
        }
    }

    private String extractContent(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("提取文件内容失败: {}", e.getMessage());
            return "";
        }
    }

    private Map<String, Object> handleDuplicateNote(NoteEntity note) {
        log.info("检测到重复笔记，返回历史结果: noteId={}", note.getId());
        return Map.of(
            "note", Map.of(
                "id", note.getId(),
                "filename", note.getOriginalFilename(),
                "analyzeStatus", note.getAnalyzeStatus() != null ? note.getAnalyzeStatus().name() : AsyncTaskStatus.PENDING.name()
            ),
            "duplicate", true
        );
    }
}
