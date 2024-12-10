package io.woco.cloud_iam_manager.facade.impl;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.woco.cloud_iam_manager.config.CloudConfig;
import io.woco.cloud_iam_manager.config.K8sConfig;
import io.woco.cloud_iam_manager.enums.K8SEventType;
import io.woco.cloud_iam_manager.facade.ServiceAccountsFacade;
import io.woco.cloud_iam_manager.services.CloudServiceIam;
import io.woco.cloud_iam_manager.services.K8sNamespaceService;
import io.woco.cloud_iam_manager.services.K8sServiceAccountService;
import io.woco.cloud_iam_manager.utils.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ServiceAccountsFacadeImpl implements ServiceAccountsFacade {
    private final K8sServiceAccountService k8sServiceAccountService;
    private final K8sNamespaceService k8sNamespaceService;
    private final CloudServiceIam cloudServiceIam;
    private final K8sConfig k8sConfig;
    private final CloudConfig cloudConfig;

    private static final Map<String, String> cache = new HashMap<>();

    @Override
    public void sync(boolean useCache) throws ApiException {
        List<String> namespaces = k8sNamespaceService.getAllNamespacesByLabel(k8sConfig);
        if (namespaces == null || namespaces.isEmpty()) {
            LogUtil.debug("Could not get Namespaces to watch, Could not find Namespaces with labels: " + k8sConfig.getNamespaceLabelToWatch());
            cache.clear();
            return;
        }
        LogUtil.debug("Namespaces found: " + namespaces);

        List<V1ServiceAccount> serviceAccounts = k8sServiceAccountService.getAllServiceAccounts(namespaces);
        if (serviceAccounts == null || serviceAccounts.isEmpty()) {
            LogUtil.debug("Could not get ServiceAccount objects, Could not find ServiceAccount objects in the provided namespaces: " + namespaces);
            cache.clear();
            return;
        }
        LogUtil.debug("ServiceAccounts found: " + serviceAccounts.stream().map(serviceAccount -> serviceAccount.getMetadata().getName()).toList());

        serviceAccounts = serviceAccounts.stream().filter(serviceAccount ->
                serviceAccount.getMetadata() != null &&
                        serviceAccount.getMetadata().getAnnotations() != null &&
                        serviceAccount.getMetadata().getAnnotations().containsKey(k8sConfig.getSaIamAnnotation()) &&
                        serviceAccount.getMetadata().getAnnotations().get(k8sConfig.getSaIamAnnotation()) != null
        ).toList();

        LogUtil.debug("ServiceAccounts found with annotation '" + k8sConfig.getSaIamAnnotation() + "': " + serviceAccounts.stream().map(serviceAccount -> serviceAccount.getMetadata().getName()).toList());

        if (useCache) {
            Set<String> cacheServiceAccountsNames = new HashSet<>(cache.keySet());
            List<String> clusterSaNames = serviceAccounts.stream().map(serviceAccount -> serviceAccount.getMetadata().getName()).toList();
            cacheServiceAccountsNames.forEach(name -> {
                if (!clusterSaNames.contains(name)) {
                    cache.remove(name);
                }
            });

            List<V1ServiceAccount> changedServiceAccounts = new ArrayList<>();
            for (V1ServiceAccount serviceAccount : serviceAccounts) {
                String serviceAccountRoleCache = cache.get(serviceAccount.getMetadata().getName());

                if (serviceAccountRoleCache != null && serviceAccountRoleCache.equals(serviceAccount.getMetadata().getAnnotations().get(k8sConfig.getSaIamAnnotation()))) {
                    LogUtil.debug("Skipping ServiceAccount: '" + serviceAccount.getMetadata().getName() +
                            "' in namespace: '" + serviceAccount.getMetadata().getNamespace() +
                            "' due to no changes identified in the annotation: '" + k8sConfig.getSaIamAnnotation() + "'");
                    continue;
                }
                changedServiceAccounts.add(serviceAccount);
            }
            serviceAccounts = changedServiceAccounts;
            LogUtil.debug("ServiceAccounts found with changes: " + serviceAccounts.stream().map(serviceAccount -> serviceAccount.getMetadata().getName()).toList());
        }

        if (serviceAccounts.isEmpty()) {
            LogUtil.debug("Could not find changes in ServiceAccount objects in the provided namespaces: " + namespaces);
            return;
        }

        for (V1ServiceAccount serviceAccount : serviceAccounts) {
            String role = serviceAccount.getMetadata().getAnnotations().get(k8sConfig.getSaIamAnnotation());

            LogUtil.debug("Processing ServiceAccount: '" + serviceAccount.getMetadata().getName() +
                    "' in namespace: '" + serviceAccount.getMetadata().getNamespace() +
                    "' with role: '" + role + "'");

            try {
                cloudServiceIam.bindIamToServiceAccount(role, serviceAccount.getMetadata().getNamespace(), serviceAccount.getMetadata().getName(), cloudConfig.isPreserveIamBindings());
                cache.put(serviceAccount.getMetadata().getName(), role);
            } catch (Exception e) {
                LogUtil.error("Error while binding IAM to ServiceAccount: '" + serviceAccount.getMetadata().getName() +
                        "' in namespace: '" + serviceAccount.getMetadata().getNamespace() +
                        "' with role: '" + role + "' error: '" + e.getMessage() + "'");

                try {
                    k8sServiceAccountService.addEventToServiceAccount(serviceAccount, K8SEventType.ERROR.getType(), "Bind IAM", e.getMessage());
                } catch (ApiException e1) {
                    LogUtil.error(e1.getMessage());
                }
            }
        }

    }
}
