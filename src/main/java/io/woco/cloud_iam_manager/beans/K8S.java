package io.woco.cloud_iam_manager.beans;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class K8S {
    @Bean
    public ApiClient apiClient() {
        try {
            return Config.defaultClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CoreV1Api coreV1Api() {
        return new CoreV1Api(apiClient());
    }

}
