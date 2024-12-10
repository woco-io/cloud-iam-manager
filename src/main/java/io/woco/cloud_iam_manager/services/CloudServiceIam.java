package io.woco.cloud_iam_manager.services;

public interface CloudServiceIam {
    void bindIamToServiceAccount(String iamResourceId, String k8sServiceAccountNamespace, String k8sServiceAccountName, boolean isPreserveExistingBindings);
}
