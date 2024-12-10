package io.woco.cloud_iam_manager.services.impl;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ObjectReference;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.woco.cloud_iam_manager.config.K8sConfig;
import io.woco.cloud_iam_manager.services.K8sServiceAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class K8sServiceAccountServiceImpl implements K8sServiceAccountService {
    private final CoreV1Api api;
    private final K8sConfig k8sConfig;

    @Override
    public void addEventToServiceAccount(V1ServiceAccount serviceAccount, String type, String reason, String message) throws ApiException {
        api.createNamespacedEvent(
                serviceAccount.getMetadata().getNamespace(),
                new CoreV1Event()
                        .type(type)
                        .reason(reason)
                        .message(message)
                        .firstTimestamp(OffsetDateTime.now().minusHours(k8sConfig.getEventTimingMinusHours()))
                        .metadata(
                                new V1ObjectMeta().generateName(serviceAccount.getMetadata().getName())
                        )
                        .involvedObject(
                                new V1ObjectReference()
                                        .apiVersion("V1")
                                        .kind("ServiceAccount")
                                        .name(serviceAccount.getMetadata().getName())
                                        .uid(serviceAccount.getMetadata().getUid())
                                        .namespace(serviceAccount.getMetadata().getNamespace())
                        )
        ).execute();
    }

    @Override
    public V1ServiceAccount getServiceAccount(String namespace, String serviceAccountName) throws ApiException {
        return api.readNamespacedServiceAccount(serviceAccountName, namespace).execute();
    }

    @Override
    public List<V1ServiceAccount> getAllServiceAccounts(String namespace) throws ApiException {
        return api.listNamespacedServiceAccount(namespace).execute().getItems().stream().toList();
    }

    @Override
    public List<V1ServiceAccount> getAllServiceAccounts(String namespace, String label) throws ApiException {
        return api.listNamespacedServiceAccount(namespace).labelSelector(label).execute().getItems().stream().toList();
    }

    @Override
    public List<V1ServiceAccount> getAllServiceAccounts(List<String> namespaces) throws ApiException {
        return api.listServiceAccountForAllNamespaces().execute().getItems().stream()
                .filter(serviceAccount -> serviceAccount.getMetadata() != null && serviceAccount.getMetadata().getNamespace() != null && namespaces.contains(serviceAccount.getMetadata().getNamespace()))
                .toList();
    }

    @Override
    public List<V1ServiceAccount> getAllServiceAccounts(List<String> namespaces, String label) throws ApiException {
        return api.listServiceAccountForAllNamespaces().labelSelector(label).execute().getItems().stream()
                .filter(serviceAccount -> serviceAccount.getMetadata() != null && serviceAccount.getMetadata().getNamespace() != null && namespaces.contains(serviceAccount.getMetadata().getNamespace()))
                .toList();
    }
}
