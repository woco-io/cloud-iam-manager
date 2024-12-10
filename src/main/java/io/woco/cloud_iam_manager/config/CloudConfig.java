package io.woco.cloud_iam_manager.config;

import io.woco.cloud_iam_manager.enums.CloudProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CloudConfig {

    @Value("${app-conf.cloud-config.cloud-provider}")
    private CloudProvider cloudProvider;

    @Value("${app-conf.cloud-config.iam-binding-role}")
    private String iamBindingRole;

    @Value("${app-conf.cloud-config.project-id}")
    private String gcpProjectId;

    @Value("${app-conf.cloud-config.is-preserve-iam-bindings}")
    private boolean isPreserveIamBindings;

}
