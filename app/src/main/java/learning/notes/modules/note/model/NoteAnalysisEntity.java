package learning.notes.modules.note.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 笔记分析结果实体
 * Note Analysis Entity
 */
@Entity
@Table(name = "note_analyses", indexes = {
    @Index(name = "idx_note_id", columnList = "note_id")
})
public class NoteAnalysisEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "note_id", nullable = false)
    private Long noteId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false, insertable = false, updatable = false)
    private NoteEntity note;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String knowledgePointsJson;
    
    @Column(columnDefinition = "TEXT")
    private String suggestionsJson;
    
    private String masteryLevel;
    
    private Integer difficulty;
    
    private Integer estimatedStudyMinutes;
    
    @Column(columnDefinition = "TEXT")
    private String prerequisitesJson;
    
    @Column(nullable = false)
    private LocalDateTime analyzedAt;
    
    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getNoteId() {
        return noteId;
    }
    
    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }
    
    public NoteEntity getNote() {
        return note;
    }
    
    public void setNote(NoteEntity note) {
        this.note = note;
        if (note != null) {
            this.noteId = note.getId();
        }
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getKnowledgePointsJson() {
        return knowledgePointsJson;
    }
    
    public void setKnowledgePointsJson(String knowledgePointsJson) {
        this.knowledgePointsJson = knowledgePointsJson;
    }
    
    public String getSuggestionsJson() {
        return suggestionsJson;
    }
    
    public void setSuggestionsJson(String suggestionsJson) {
        this.suggestionsJson = suggestionsJson;
    }
    
    public String getMasteryLevel() {
        return masteryLevel;
    }
    
    public void setMasteryLevel(String masteryLevel) {
        this.masteryLevel = masteryLevel;
    }
    
    public Integer getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
    
    public Integer getEstimatedStudyMinutes() {
        return estimatedStudyMinutes;
    }
    
    public void setEstimatedStudyMinutes(Integer estimatedStudyMinutes) {
        this.estimatedStudyMinutes = estimatedStudyMinutes;
    }
    
    public String getPrerequisitesJson() {
        return prerequisitesJson;
    }
    
    public void setPrerequisitesJson(String prerequisitesJson) {
        this.prerequisitesJson = prerequisitesJson;
    }
    
    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }
    
    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
