package learning.notes.modules.note;

import learning.notes.common.result.Result;
import learning.notes.modules.note.model.NoteDetailDTO;
import learning.notes.modules.note.model.NoteListItemDTO;
import learning.notes.modules.note.model.PdfExportResult;
import learning.notes.modules.note.service.NoteDeleteService;
import learning.notes.modules.note.service.NoteHistoryService;
import learning.notes.modules.note.service.NoteUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 笔记控制器
 * Note Controller for upload, management and analysis
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class NoteController {

    private final NoteUploadService uploadService;
    private final NoteDeleteService deleteService;
    private final NoteHistoryService historyService;

    /**
     * 上传笔记文件并开始分析
     *
     * @param file 笔记文件（支持PDF、Word、Markdown等）
     * @return 上传结果，包含笔记ID和处理状态
     */
    @PostMapping(value = "/api/notes/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> uploadAndAnalyze(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = uploadService.uploadAndAnalyze(file);
        boolean isDuplicate = (Boolean) result.get("duplicate");
        if (isDuplicate) {
            return Result.success("检测到相同笔记，已返回历史分析结果", result);
        }
        return Result.success(result);
    }

    /**
     * 获取所有笔记列表
     */
    @GetMapping("/api/notes")
    public Result<List<NoteListItemDTO>> getAllNotes() {
        List<NoteListItemDTO> notes = historyService.getAllNotes();
        return Result.success(notes);
    }

    /**
     * 获取笔记详情（包含分析结果）
     */
    @GetMapping("/api/notes/{id}/detail")
    public Result<NoteDetailDTO> getNoteDetail(@PathVariable Long id) {
        NoteDetailDTO detail = historyService.getNoteDetail(id);
        return Result.success(detail);
    }

    /**
     * 导出笔记分析报告为PDF
     */
    @GetMapping("/api/notes/{id}/export")
    public ResponseEntity<byte[]> exportAnalysisPdf(@PathVariable Long id) {
        try {
            PdfExportResult result = historyService.exportAnalysisPdf(id);
            String filename = URLEncoder.encode(result.filename(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(result.pdfBytes());
        } catch (Exception e) {
            log.error("导出PDF失败: noteId={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除笔记
     *
     * @param id 笔记ID
     * @return 删除结果
     */
    @DeleteMapping("/api/notes/{id}")
    public Result<Void> deleteNote(@PathVariable Long id) {
        deleteService.deleteNote(id);
        return Result.success(null);
    }

    /**
     * 重新分析笔记（手动重试）
     * 用于分析失败后的重试
     *
     * @param id 笔记ID
     * @return 结果
     */
    @PostMapping("/api/notes/{id}/reanalyze")
    public Result<Void> reanalyze(@PathVariable Long id) {
        uploadService.reanalyze(id);
        return Result.success(null);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/api/notes/health")
    public Result<Map<String, String>> health() {
        return Result.success(Map.of(
            "status", "UP",
            "service", "Smart Learning Notes - Note Service"
        ));
    }

}
