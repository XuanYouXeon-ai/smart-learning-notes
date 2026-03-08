package learning.notes.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {

    private StorageConfig storage;
    private CorsConfig cors;

    @Data
    public static class StorageConfig {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String region = "us-east-1";
    }

    @Data
    public static class CorsConfig {
        private String allowedOrigins;
    }
}
