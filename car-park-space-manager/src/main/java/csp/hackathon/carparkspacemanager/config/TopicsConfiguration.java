package csp.hackathon.carparkspacemanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "topics")
public class TopicsConfiguration {
    @NotNull
    private String barrierEventTopic;

    @NotNull
    private String availabilityOutputTopic;
}
