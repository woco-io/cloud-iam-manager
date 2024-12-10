package io.woco.cloud_iam_manager.beans;

import com.google.cloud.iam.admin.v1.IAMClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@ConditionalOnExpression("'${app-conf.cloud-config.cloud-provider}'.equals(\"GCP\")")
@Configuration
public class GCP {

    @Bean
    public IAMClient iamClient() {
        try {
            return IAMClient.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
