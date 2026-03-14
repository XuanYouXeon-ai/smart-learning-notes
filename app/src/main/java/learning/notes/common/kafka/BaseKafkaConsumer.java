package learning.notes.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Kafka 消费者基类
 * @param <T> 消息类型
 */
@Slf4j
public abstract class BaseKafkaConsumer<T> {

    protected abstract String getTopic();
    protected abstract String getGroupId();
    protected abstract T deserialize(String value);
    protected abstract void process(T message);
    protected abstract String messageIdentifier(T message);

    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String value = record.value();
        log.info("收到消息: topic={}, partition={}, offset={}, key={}", 
            record.topic(), record.partition(), record.offset(), record.key());
        
        try {
            T message = deserialize(value);
            if (message == null) {
                log.warn("消息反序列化失败，跳过处理: value={}", value);
                ack.acknowledge();
                return;
            }
            
            process(message);
            ack.acknowledge();
            log.info("消息处理完成: {}", messageIdentifier(message));
            
        } catch (Exception e) {
            String messageId = "unknown";
            try {
                T msg = deserialize(value);
                if (msg != null) {
                    messageId = messageIdentifier(msg);
                }
            } catch (Exception ignored) {}
            
            log.error("消息处理失败: {}, error={}", messageId, e.getMessage(), e);
            handleProcessingError(messageId, e);
            ack.acknowledge();
        }
    }

    protected void handleProcessingError(String messageId, Exception e) {
        log.error("消息处理错误: messageId={}, error={}", messageId, e.getMessage());
    }
}
