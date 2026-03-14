package learning.notes.modules.note.repository;

import learning.notes.modules.note.model.NoteAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 笔记分析结果Repository
 */
@Repository
public interface NoteAnalysisRepository extends JpaRepository<NoteAnalysisEntity, Long> {
    
    /**
     * 根据笔记ID查找分析结果
     */
    Optional<NoteAnalysisEntity> findByNoteId(Long noteId);
    
    /**
     * 根据笔记ID删除分析结果
     */
    void deleteByNoteId(Long noteId);
}
