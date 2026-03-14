package learning.notes.modules.note.repository;

import learning.notes.modules.note.model.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 笔记Repository
 */
@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    
    /**
     * 根据文件哈希查找笔记（用于去重）
     */
    Optional<NoteEntity> findByFileHash(String fileHash);
    
    /**
     * 检查文件哈希是否存在
     */
    boolean existsByFileHash(String fileHash);
}
