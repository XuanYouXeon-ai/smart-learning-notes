package learning.notes.modules.note.service;

import learning.notes.common.exception.BusinessException;
import learning.notes.common.exception.ErrorCode;
import learning.notes.common.model.AsyncTaskStatus;
import learning.notes.modules.note.model.NoteAnalysisEntity;
import learning.notes.modules.note.model.NoteDetailDTO;
import learning.notes.modules.note.model.NoteEntity;
import learning.notes.modules.note.model.NoteListItemDTO;
import learning.notes.modules.note.model.PdfExportResult;
import learning.notes.modules.note.repository.NoteAnalysisRepository;
import learning.notes.modules.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 笔记历史服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteHistoryService {

    private final NoteRepository noteRepository;
    private final NoteAnalysisRepository analysisRepository;

    /**
     * 获取所有笔记列表
     */
    public List<NoteListItemDTO> getAllNotes() {
        return noteRepository.findAll().stream()
            .map(this::toListItemDTO)
            .toList();
    }

    /**
     * 获取笔记详情
     */
    public NoteDetailDTO getNoteDetail(Long id) {
        NoteEntity note = noteRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTE_NOT_FOUND, "笔记不存在"));

        NoteAnalysisEntity analysis = analysisRepository.findByNoteId(id).orElse(null);

        return new NoteDetailDTO(
            note.getId(),
            note.getOriginalFilename(),
            note.getOriginalFilename(),
            note.getContentType(),
            note.getFileSize() != null ? note.getFileSize() : 0,
            note.getAnalyzeStatus() != null ? note.getAnalyzeStatus() : AsyncTaskStatus.PENDING,
            note.getAnalyzeError(),
            note.getUploadedAt(),
            analysis != null ? analysis.getAnalyzedAt() : null,
            analysis != null ? analysis.getSummary() : null,
            analysis != null ? analysis.getKnowledgePointsJson() : null,
            analysis != null ? analysis.getSuggestionsJson() : null,
            analysis != null ? analysis.getMasteryLevel() : null,
            analysis != null ? analysis.getDifficulty() : null,
            analysis != null ? analysis.getEstimatedStudyMinutes() : null,
            analysis != null ? analysis.getPrerequisitesJson() : null
        );
    }

    /**
     * 导出分析报告为PDF
     */
    public PdfExportResult exportAnalysisPdf(Long id) {
        NoteDetailDTO detail = getNoteDetail(id);
        String filename = detail.filename() + "_analysis.pdf";
        byte[] pdfBytes = generatePdf(detail);
        return new PdfExportResult(filename, pdfBytes);
    }

    private NoteListItemDTO toListItemDTO(NoteEntity note) {
        NoteAnalysisEntity analysis = analysisRepository.findByNoteId(note.getId()).orElse(null);
        return new NoteListItemDTO(
            note.getId(),
            note.getOriginalFilename(),
            note.getOriginalFilename(),
            note.getContentType(),
            note.getFileSize() != null ? note.getFileSize() : 0,
            note.getAnalyzeStatus() != null ? note.getAnalyzeStatus() : AsyncTaskStatus.PENDING,
            note.getUploadedAt(),
            analysis != null ? analysis.getAnalyzedAt() : null,
            analysis != null ? countKnowledgePoints(analysis.getKnowledgePointsJson()) : 0
        );
    }

    private int countKnowledgePoints(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return 0;
        }
        return json.split("\",\"").length;
    }

    private byte[] generatePdf(NoteDetailDTO detail) {
        String content = "笔记分析报告\n\n" +
            "文件名: " + detail.filename() + "\n" +
            "状态: " + detail.analyzeStatus() + "\n" +
            "摘要: " + (detail.summary() != null ? detail.summary() : "暂无") + "\n";
        return content.getBytes();
    }
}
