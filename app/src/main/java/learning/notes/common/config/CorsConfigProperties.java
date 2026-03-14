package learning.notes.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsConfigProperties {

    private String allowedOrigins;
}
