package io.woco.cloud_iam_manager.exceptions;

import lombok.Getter;

@Getter
public class BaseK8sIamManagerRuntimeException extends RuntimeException {
    private final String description;

    protected BaseK8sIamManagerRuntimeException(String message, String description) {
        super(message);
        this.description = description;
    }
}
