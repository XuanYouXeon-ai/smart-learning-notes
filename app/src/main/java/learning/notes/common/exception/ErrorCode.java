package learning.notes.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    INVALID_PARAM(1001, "参数无效"),
    SYSTEM_ERROR(1002, "系统错误"),

    FILE_UPLOAD_FAILED(2001, "文件上传失败"),
    FILE_NOT_FOUND(2002, "文件不存在"),
    INVALID_FILE_TYPE(2003, "不支持的文件类型"),
    FILE_TOO_LARGE(2004, "文件大小超出限制"),

    NOTE_NOT_FOUND(3001, "笔记不存在"),
    NOTE_PARSE_FAILED(3002, "笔记解析失败"),
    ANALYSIS_FAILED(3003, "笔记分析失败"),
    KNOWLEDGE_EXTRACTION_FAILED(3004, "知识点提取失败");

    private final int code;
    private final String message;
}
