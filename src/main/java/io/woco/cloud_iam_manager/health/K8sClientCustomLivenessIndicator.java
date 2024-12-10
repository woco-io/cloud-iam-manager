package io.woco.cloud_iam_manager.health;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.VersionApi;
import io.woco.cloud_iam_manager.config.HealthConfig;
import io.woco.cloud_iam_manager.utils.LogUtil;
import org.springframework.boot.actuate.availability.LivenessStateHealthIndicator;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.LivenessState;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * The type K 8 s client custom liveness indicator.
 */
@Component
public class K8sClientCustomLivenessIndicator extends LivenessStateHealthIndicator {
    private final HealthConfig healthConfig;
    private final ApiClient apiClient;

    private VersionApi versionApi;

    /**
     * Instantiates a new K 8 s client custom liveness indicator.
     *
     * @param availability the availability
     * @param healthConfig the health config
     * @param apiClient    the api client
     */
    public K8sClientCustomLivenessIndicator(ApplicationAvailability availability, HealthConfig healthConfig, ApiClient apiClient) {
        super(availability);

        this.healthConfig = healthConfig;
        this.apiClient = apiClient;
    }

    @PostConstruct
    private void createVersionApi() {
        versionApi = new VersionApi(apiClient);
    }

    @Override
    protected AvailabilityState getState(ApplicationAvailability applicationAvailability) {
        try {
            versionApi.getCode();
        } catch (Exception e) {
            LogUtil.error(((ApiException) e).getResponseBody());
            if (healthConfig.getIncludeK8sClientHealth()) {
                return LivenessState.BROKEN;
            }
        }

        return LivenessState.CORRECT;
    }

}
