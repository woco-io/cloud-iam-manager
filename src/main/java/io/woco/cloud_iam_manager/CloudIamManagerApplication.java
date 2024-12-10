package io.woco.cloud_iam_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
public class CloudIamManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudIamManagerApplication.class, args);
    }

}
