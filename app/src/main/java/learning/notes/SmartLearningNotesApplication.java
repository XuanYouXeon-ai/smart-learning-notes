package learning.notes;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartLearningNotesApplication {

    public static void main(String[] args) {
        String projectRoot = System.getProperty("user.dir");
        if (projectRoot.endsWith("app")) {
            projectRoot = projectRoot.substring(0, projectRoot.length() - 4);
        }

        Dotenv dotenv = Dotenv.configure()
            .directory(projectRoot)
            .ignoreIfMissing()
            .load();

        String apiKey = dotenv.get("AI_BAILIAN_API_KEY");
        System.out.println("===== .env path: " + projectRoot + "/.env =====");
        System.out.println("===== AI_BAILIAN_API_KEY loaded: " +
            (apiKey != null ? apiKey.substring(0, 10) + "..." : "NOT FOUND") + " =====");

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(SmartLearningNotesApplication.class, args);
    }
}
