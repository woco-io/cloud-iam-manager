package io.woco.cloud_iam_manager.beans;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sts.StsClient;

@ConditionalOnExpression("'${app-conf.cloud-config.cloud-provider}'.equals(\"AWS\")")
@Configuration
public class AWS {

    @Bean
    public StsClient stsClient() {
        return StsClient.create();
    }

}
