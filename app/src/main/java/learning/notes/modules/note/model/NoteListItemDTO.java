package learning.notes.modules.note.model;

import learning.notes.common.model.AsyncTaskStatus;

import java.time.LocalDateTime;

/**
 * 笔记列表项DTO
 */
public record NoteListItemDTO(
    Long id,
    String title,
    String filename,
    String fileType,
    long fileSize,
    AsyncTaskStatus analyzeStatus,
    LocalDateTime uploadTime,
    LocalDateTime analysisTime,
    Integer knowledgePointCount
) {}
