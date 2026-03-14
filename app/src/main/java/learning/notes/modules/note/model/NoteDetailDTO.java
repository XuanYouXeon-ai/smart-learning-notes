package learning.notes.modules.note.model;

import learning.notes.common.model.AsyncTaskStatus;

import java.time.LocalDateTime;

/**
 * 笔记详情DTO
 */
public record NoteDetailDTO(
    Long id,
    String title,
    String filename,
    String fileType,
    long fileSize,
    AsyncTaskStatus analyzeStatus,
    String analyzeError,
    LocalDateTime uploadTime,
    LocalDateTime analysisTime,
    String summary,
    String knowledgePointsJson,
    String suggestionsJson,
    String masteryLevel,
    Integer difficulty,
    Integer estimatedStudyMinutes,
    String prerequisitesJson
) {}
