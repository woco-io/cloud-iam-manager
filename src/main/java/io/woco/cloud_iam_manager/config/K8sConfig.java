package io.woco.cloud_iam_manager.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class K8sConfig {

    @Value("${app-conf.k8s-config.namespace-label}")
    private String namespaceLabelToWatch;

    @Value("${app-conf.k8s-config.eventTimingMinusHours}")
    private long eventTimingMinusHours;

    @Value("${app-conf.k8s-config.service-account-iam-annotation}")
    private String saIamAnnotation;

}
