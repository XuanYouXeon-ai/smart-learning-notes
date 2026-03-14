package learning.notes.modules.note.service;

import learning.notes.common.exception.BusinessException;
import learning.notes.common.exception.ErrorCode;
import learning.notes.modules.note.model.NoteEntity;
import learning.notes.modules.note.repository.NoteAnalysisRepository;
import learning.notes.modules.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 笔记删除服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteDeleteService {

    private final NoteRepository noteRepository;
    private final NoteAnalysisRepository analysisRepository;

    /**
     * 删除笔记
     */
    @Transactional
    public void deleteNote(Long id) {
        NoteEntity note = noteRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTE_NOT_FOUND, "笔记不存在"));

        log.info("开始删除笔记: noteId={}", id);

        analysisRepository.findByNoteId(id).ifPresent(analysis -> {
            analysisRepository.delete(analysis);
            log.info("已删除笔记分析结果: noteId={}", id);
        });

        noteRepository.delete(note);
        log.info("笔记删除完成: noteId={}", id);
    }
}
