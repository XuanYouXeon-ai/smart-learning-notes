package learning.notes.modules.note.listener;

import learning.notes.common.kafka.KafkaTopics;
import learning.notes.common.model.AsyncTaskStatus;
import learning.notes.modules.note.model.NoteAnalysisEntity;
import learning.notes.modules.note.model.NoteEntity;
import learning.notes.modules.note.repository.NoteAnalysisRepository;
import learning.notes.modules.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 笔记分析任务消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NoteAnalysisConsumer {

    private final NoteRepository noteRepository;
    private final NoteAnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;

    public record NoteAnalysisMessage(Long noteId, String content, int retryCount) {}

    @KafkaListener(
        topics = KafkaTopics.NOTE_ANALYSIS,
        groupId = KafkaTopics.NOTE_ANALYSIS_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String value = record.value();
        log.info("收到消息: topic={}, partition={}, offset={}, key={}", 
            record.topic(), record.partition(), record.offset(), record.key());
        
        try {
            NoteAnalysisMessage message = deserialize(value);
            if (message == null) {
                log.warn("消息反序列化失败，跳过处理: value={}", value);
                ack.acknowledge();
                return;
            }
            
            process(message);
            ack.acknowledge();
            log.info("消息处理完成: noteId={}", message.noteId());
            
        } catch (Exception e) {
            log.error("消息处理失败: error={}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    private NoteAnalysisMessage deserialize(String value) {
        try {
            return objectMapper.readValue(value, NoteAnalysisMessage.class);
        } catch (Exception e) {
            log.error("反序列化消息失败: {}", e.getMessage());
            return null;
        }
    }

    private void process(NoteAnalysisMessage message) {
        Long noteId = message.noteId();
        
        if (!noteRepository.existsById(noteId)) {
            log.warn("笔记已被删除，跳过分析任务: noteId={}", noteId);
            return;
        }

        updateStatus(noteId, AsyncTaskStatus.PROCESSING, null);

        try {
            NoteAnalysisEntity analysis = performAnalysis(message);
            analysisRepository.save(analysis);
            updateStatus(noteId, AsyncTaskStatus.COMPLETED, null);
            log.info("笔记分析完成: noteId={}", noteId);
            
        } catch (Exception e) {
            log.error("笔记分析失败: noteId={}, error={}", noteId, e.getMessage(), e);
            updateStatus(noteId, AsyncTaskStatus.FAILED, truncateError(e.getMessage()));
        }
    }

    private NoteAnalysisEntity performAnalysis(NoteAnalysisMessage message) {
        NoteEntity note = noteRepository.findById(message.noteId()).orElseThrow();
        
        NoteAnalysisEntity analysis = new NoteAnalysisEntity();
        analysis.setNoteId(message.noteId());
        analysis.setNote(note);
        analysis.setTitle("笔记分析结果");
        analysis.setSummary("AI 分析摘要 - 待实现");
        analysis.setKnowledgePointsJson("[]");
        analysis.setSuggestionsJson("[]");
        analysis.setMasteryLevel("BEGINNER");
        analysis.setDifficulty(3);
        analysis.setEstimatedStudyMinutes(30);
        return analysis;
    }

    private void updateStatus(Long noteId, AsyncTaskStatus status, String error) {
        noteRepository.findById(noteId).ifPresent(note -> {
            note.setAnalyzeStatus(status);
            if (error != null) {
                note.setAnalyzeError(error);
            }
            noteRepository.save(note);
        });
    }

    private String truncateError(String error) {
        if (error == null) return null;
        return error.length() > 500 ? error.substring(0, 500) : error;
    }
}
