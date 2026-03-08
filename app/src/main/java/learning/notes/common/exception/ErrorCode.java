package learning.notes.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    FILE_UPLOAD_FAILED(1001, "文件上传失败"),
    FILE_NOT_FOUND(1002, "文件不存在"),
    INVALID_FILE_TYPE(1003, "不支持的文件类型"),
    NOTE_NOT_FOUND(2001, "笔记不存在"),
    ANALYSIS_FAILED(2002, "笔记分析失败"),
    KNOWLEDGE_EXTRACTION_FAILED(2003, "知识点提取失败");

    private final int code;
    private final String message;
}
