package io.woco.cloud_iam_manager.services.impl;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.woco.cloud_iam_manager.config.K8sConfig;
import io.woco.cloud_iam_manager.services.K8sNamespaceService;
import io.woco.cloud_iam_manager.utils.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type K 8 s namespace service.
 */
@RequiredArgsConstructor
@Service
public class K8sNamespaceServiceImpl implements K8sNamespaceService {
    private final CoreV1Api coreV1Api;

    /**
     * Retrieves all Kubernetes namespaces that match the specified label.
     *
     * @param k8sConfig the configuration containing the label to watch for namespaces
     * @return a list of namespace names that match the specified label
     * @throws ApiException if an error occurs while attempting to retrieve the namespaces
     */
    @Override
    public List<String> getAllNamespacesByLabel(K8sConfig k8sConfig) throws ApiException {
        LogUtil.debug("Retrieving all namespaces by label: '" + k8sConfig.getNamespaceLabelToWatch() + "'");

        V1NamespaceList namespaceList;
        if (k8sConfig.getNamespaceLabelToWatch().equalsIgnoreCase("all")) {
            namespaceList = coreV1Api.listNamespace().timeoutSeconds(10).execute();
        } else {
            namespaceList = coreV1Api.listNamespace().labelSelector(k8sConfig.getNamespaceLabelToWatch()).timeoutSeconds(10).execute();
        }

        return namespaceList.getItems()
                .stream()
                .filter(namespace -> namespace.getMetadata() != null && namespace.getMetadata().getName() != null && !namespace.getMetadata().getName().isEmpty())
                .map(namespace -> namespace.getMetadata().getName())
                .toList();
    }
}
