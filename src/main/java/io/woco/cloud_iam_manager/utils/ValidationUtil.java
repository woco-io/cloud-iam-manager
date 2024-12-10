package io.woco.cloud_iam_manager.utils;

import io.woco.cloud_iam_manager.enums.CloudProvider;
import io.woco.cloud_iam_manager.exceptions.UnknownCloudProviderException;

import java.util.Arrays;

public class ValidationUtil {

    public static CloudProvider validateCloudProvider(String cloudUpperCase) throws UnknownCloudProviderException {
        if (Arrays.stream(CloudProvider.values()).noneMatch(item -> item.name().equals(cloudUpperCase))) {
            throw new UnknownCloudProviderException("Supplied cloud provider is not supported", "supported cloud providers are: " + Arrays.toString(CloudProvider.values()));
        }

        return CloudProvider.valueOf(cloudUpperCase);
    }
}
