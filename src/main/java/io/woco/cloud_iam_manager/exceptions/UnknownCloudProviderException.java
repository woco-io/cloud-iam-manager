package io.woco.cloud_iam_manager.exceptions;

public class UnknownCloudProviderException extends BaseK8sIamManagerException {

    public UnknownCloudProviderException(String message, String description) {
        super(message, description);
    }
}
