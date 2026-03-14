package learning.notes.modules.note.listener;

import learning.notes.common.kafka.BaseKafkaProducer;
import learning.notes.common.kafka.KafkaTopics;
import learning.notes.common.model.AsyncTaskStatus;
import learning.notes.modules.note.model.NoteEntity;
import learning.notes.modules.note.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 笔记分析任务生产者
 */
@Slf4j
@Component
public class NoteAnalysisProducer extends BaseKafkaProducer<NoteAnalysisProducer.NoteAnalysisMessage> {

    private final NoteRepository noteRepository;
    private final ObjectMapper objectMapper;

    public record NoteAnalysisMessage(Long noteId, String content, int retryCount) {}

    public NoteAnalysisProducer(KafkaTemplate<String, String> kafkaTemplate, 
                                NoteRepository noteRepository,
                                ObjectMapper objectMapper) {
        super(kafkaTemplate);
        this.noteRepository = noteRepository;
        this.objectMapper = objectMapper;
    }

    public void sendAnalysisTask(Long noteId, String content) {
        send(new NoteAnalysisMessage(noteId, content, 0));
    }

    @Override
    protected String topic() {
        return KafkaTopics.NOTE_ANALYSIS;
    }

    @Override
    protected String serialize(NoteAnalysisMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("序列化消息失败: {}", e.getMessage());
            throw new RuntimeException("序列化消息失败", e);
        }
    }

    @Override
    protected String messageKey(NoteAnalysisMessage message) {
        return String.valueOf(message.noteId());
    }

    @Override
    protected void onSendFailed(NoteAnalysisMessage message, String error) {
        noteRepository.findById(message.noteId()).ifPresent(note -> {
            note.setAnalyzeStatus(AsyncTaskStatus.FAILED);
            note.setAnalyzeError(error.length() > 500 ? error.substring(0, 500) : error);
            noteRepository.save(note);
        });
    }
}
