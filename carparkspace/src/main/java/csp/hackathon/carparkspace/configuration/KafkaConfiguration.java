package csp.hackathon.carparkspace.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfiguration {
    @NotNull
    private String brokerUrl;

    @NotNull
    private String applicationId;
}
