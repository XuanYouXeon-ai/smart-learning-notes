package learning.notes.modules.note.model;

/**
 * PDF导出结果
 */
public record PdfExportResult(String filename, byte[] pdfBytes) {}
