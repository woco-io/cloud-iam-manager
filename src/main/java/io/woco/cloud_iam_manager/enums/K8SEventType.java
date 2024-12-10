package io.woco.cloud_iam_manager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The enum K 8 s event type.
 */
@Getter
@RequiredArgsConstructor
public enum K8SEventType {
    /**
     * Normal k 8 s event type.
     */
    NORMAL("Normal"),
    /**
     * Warning k 8 s event type.
     */
    WARNING("Warning"),
    /**
     * Error k 8 s event type.
     */
    ERROR("Error");

    private final String type;
}
