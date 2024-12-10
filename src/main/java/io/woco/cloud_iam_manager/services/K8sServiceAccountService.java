package io.woco.cloud_iam_manager.services;


import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ServiceAccount;

import java.util.List;


public interface K8sServiceAccountService {

    void addEventToServiceAccount(V1ServiceAccount serviceAccount, String type, String reason, String message) throws ApiException;

    V1ServiceAccount getServiceAccount(String namespace, String serviceAccountName) throws ApiException;

    List<V1ServiceAccount> getAllServiceAccounts(String namespace) throws ApiException;

    List<V1ServiceAccount> getAllServiceAccounts(String namespace, String label) throws ApiException;

    List<V1ServiceAccount> getAllServiceAccounts(List<String> namespaces) throws ApiException;

    List<V1ServiceAccount> getAllServiceAccounts(List<String> namespaces, String label) throws ApiException;

}

