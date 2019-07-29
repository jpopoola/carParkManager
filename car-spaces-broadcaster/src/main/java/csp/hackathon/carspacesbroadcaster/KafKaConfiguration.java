package csp.hackathon.carspacesbroadcaster;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafKaConfiguration {
    @NotNull
    private String topic;

    @NotNull
    private String brokerUrl;

    @NotNull
    private String groupId;

    @NotNull
    private String resetConfig;

    @NotNull
    private String pollDuration;
}
