package learning.notes.modules.note.model;

import learning.notes.common.model.AsyncTaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 笔记实体
 * Note Entity for deduplication and persistence
 */
@Entity
@Table(name = "notes", indexes = {
    @Index(name = "idx_note_hash", columnList = "fileHash", unique = true)
})
public class NoteEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 64)
    private String fileHash;
    
    @Column(nullable = false)
    private String originalFilename;
    
    private Long fileSize;
    
    private String contentType;
    
    @Column(length = 500)
    private String storageKey;
    
    @Column(length = 1000)
    private String storageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String noteText;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    private LocalDateTime lastAccessedAt;
    
    private Integer accessCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AsyncTaskStatus analyzeStatus = AsyncTaskStatus.PENDING;

    @Column(length = 500)
    private String analyzeError;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        lastAccessedAt = LocalDateTime.now();
        accessCount = 1;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFileHash() {
        return fileHash;
    }
    
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getStorageKey() {
        return storageKey;
    }
    
    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }
    
    public String getStorageUrl() {
        return storageUrl;
    }
    
    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }
    
    public String getNoteText() {
        return noteText;
    }
    
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }
    
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
    
    public Integer getAccessCount() {
        return accessCount;
    }
    
    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }
    
    public void incrementAccessCount() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }

    public AsyncTaskStatus getAnalyzeStatus() {
        return analyzeStatus;
    }

    public void setAnalyzeStatus(AsyncTaskStatus analyzeStatus) {
        this.analyzeStatus = analyzeStatus;
    }

    public String getAnalyzeError() {
        return analyzeError;
    }

    public void setAnalyzeError(String analyzeError) {
        this.analyzeError = analyzeError;
    }
}
