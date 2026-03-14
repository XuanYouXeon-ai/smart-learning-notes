package learning.notes.common.kafka;

/**
 * Kafka Topic 常量定义
 */
public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String NOTE_ANALYSIS = "note.analysis";
    public static final String KNOWLEDGE_EXTRACTION = "knowledge.extraction";
    public static final String REVIEW_PLAN = "review.plan";
    public static final String VECTORIZATION = "vectorization";

    public static final String NOTE_ANALYSIS_GROUP = "note-analysis-group";
    public static final String KNOWLEDGE_EXTRACTION_GROUP = "knowledge-extraction-group";
    public static final String REVIEW_PLAN_GROUP = "review-plan-group";
    public static final String VECTORIZATION_GROUP = "vectorization-group";
}
