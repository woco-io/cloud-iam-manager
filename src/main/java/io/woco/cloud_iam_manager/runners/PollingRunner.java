package io.woco.cloud_iam_manager.runners;

import io.kubernetes.client.openapi.ApiException;
import io.woco.cloud_iam_manager.config.GlobalConfig;
import io.woco.cloud_iam_manager.facade.ServiceAccountsFacade;
import io.woco.cloud_iam_manager.utils.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PollingRunner {
    private final ServiceAccountsFacade serviceAccountsFacade;
    private final GlobalConfig globalConfig;
//    private final K8sConfig k8sConfig;
//    private final CoreV1Api api;

    @Scheduled(fixedDelayString = "${app-conf.runners.polling-interval}")
    protected void runSyncPooling() {
        Thread currentThread = Thread.currentThread();
        final String originalThreadName = currentThread.getName();
        currentThread.setName("runSyncPooling");

        try {
            serviceAccountsFacade.sync(globalConfig.isUseCache());
        } catch (ApiException e) {
            LogUtil.error("Error found: '" + e + "'");
        } finally {
            // Restore the original thread name after execution
            currentThread.setName(originalThreadName);
        }
    }

//    @Scheduled(fixedDelayString = "10000")
//    protected void runSyncPoolingLeader() throws IOException {
//        Thread currentThread = Thread.currentThread();
//        final String originalThreadName = currentThread.getName();
//        currentThread.setName("runSyncPooling");
//
//        ApiClient client = Config.defaultClient();
//        Configuration.setDefaultApiClient(client);
//
//        final String namespace = System.getenv("POD_NAMESPACE");
//        final String name = "cloud-iam-manager-leader";
//        final String lockHolderIdentityName = System.getenv("POD_NAME") + "-" + UUID.randomUUID();
//
//        final EndpointsLock lock = new EndpointsLock(namespace, name, lockHolderIdentityName);
//        final LeaderElectionConfig leaderElectionConfig = new LeaderElectionConfig(lock, Duration.ofMillis(10000), Duration.ofMillis(8000), Duration.ofMillis(2000));
//
//        try (LeaderElector leaderElector = new LeaderElector(leaderElectionConfig)) {
//            leaderElector.run(() ->
//                    {
//                        LogUtil.info("I'm leader now!, Identity: " + lockHolderIdentityName);
//
//                        try {
//                            serviceAccountsFacade.sync(globalConfig.isUseCache());
//                        } catch (ApiException e) {
//                            LogUtil.error("Error found: '" + e + "'");
//                        } finally {
//                            // Restore the original thread name after execution
//                            currentThread.setName(originalThreadName);
//                        }
//                    },
//                    () -> {
//                        currentThread.setName(originalThreadName);
//                        LogUtil.warn("I'm not leader anymore!, Identity: " + lockHolderIdentityName);
//                    }
//            );
//        }
//    }
}
