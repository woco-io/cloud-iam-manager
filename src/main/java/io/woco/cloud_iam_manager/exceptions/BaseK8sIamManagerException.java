package io.woco.cloud_iam_manager.exceptions;

import lombok.Getter;

@Getter
public class BaseK8sIamManagerException extends Exception {
    private final String description;

    protected BaseK8sIamManagerException(String message, String description) {
        super(message);
        this.description = description;
    }
}
