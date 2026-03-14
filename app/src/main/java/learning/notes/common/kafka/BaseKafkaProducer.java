package learning.notes.common.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka 生产者基类
 * @param <T> 消息类型
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseKafkaProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;

    protected abstract String topic();
    protected abstract String serialize(T message);
    protected abstract String messageKey(T message);

    public void send(T message) {
        String key = messageKey(message);
        String value = serialize(message);
        
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic(), key, value);
        
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("发送消息失败: topic={}, key={}, error={}", topic(), key, ex.getMessage());
                onSendFailed(message, ex.getMessage());
            } else {
                log.info("消息发送成功: topic={}, key={}, partition={}, offset={}", 
                    topic(), key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                onSendSuccess(message);
            }
        });
    }

    public void sendSync(T message) {
        String key = messageKey(message);
        String value = serialize(message);
        
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic(), key, value).get();
            log.info("消息发送成功: topic={}, key={}, partition={}, offset={}", 
                topic(), key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            onSendSuccess(message);
        } catch (Exception e) {
            log.error("发送消息失败: topic={}, key={}, error={}", topic(), key, e.getMessage());
            onSendFailed(message, e.getMessage());
            throw new RuntimeException("发送消息失败", e);
        }
    }

    protected void onSendSuccess(T message) {
    }

    protected void onSendFailed(T message, String error) {
    }
}
