package io.woco.cloud_iam_manager.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GlobalConfig {

    @Value("${app-conf.global.is-use-cache}")
    private boolean isUseCache;

}
