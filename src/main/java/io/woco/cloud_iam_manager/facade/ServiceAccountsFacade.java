package io.woco.cloud_iam_manager.facade;

import io.kubernetes.client.openapi.ApiException;

public interface ServiceAccountsFacade {

    void sync(boolean useCache) throws ApiException;
}
