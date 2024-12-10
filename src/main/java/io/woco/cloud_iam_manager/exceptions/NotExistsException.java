package io.woco.cloud_iam_manager.exceptions;

public class NotExistsException extends BaseK8sIamManagerException {

    public NotExistsException(String message, String description) {
        super(message, description);
    }
}
