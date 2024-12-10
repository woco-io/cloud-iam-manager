package io.woco.cloud_iam_manager.services;

import io.kubernetes.client.openapi.ApiException;
import io.woco.cloud_iam_manager.config.K8sConfig;

import java.util.List;

/**
 * The interface K 8 s namespace service.
 */
public interface K8sNamespaceService {

    List<String> getAllNamespacesByLabel(K8sConfig k8sConfig) throws ApiException;
}
