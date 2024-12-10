package io.woco.cloud_iam_manager.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class HealthConfig {

    @Value("${app-conf.health.liveness.k8s-client.include}")
    private Boolean includeK8sClientHealth;

}
