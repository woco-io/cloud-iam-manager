package io.woco.cloud_iam_manager.services.impl;

import com.google.cloud.iam.admin.v1.IAMClient;
import com.google.iam.v1.Binding;
import com.google.iam.v1.Policy;
import io.woco.cloud_iam_manager.config.CloudConfig;
import io.woco.cloud_iam_manager.services.CloudServiceIam;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@ConditionalOnExpression("'${app-conf.cloud-config.cloud-provider}'.equals(\"GCP\")")
@RequiredArgsConstructor
@Service
public class CloudServiceGcpIamImpl implements CloudServiceIam {
    private final CloudConfig cloudConfig;
    private final IAMClient iamClient;

    @Override
    public void bindIamToServiceAccount(String iamResourceId, String k8sServiceAccountNamespace, String k8sServiceAccountName, boolean isPreserveExistingBindings) {
        String resourceName = "projects/%s/serviceAccounts/%s".formatted(cloudConfig.getGcpProjectId(), iamResourceId);

        List<Binding> bindings = new ArrayList<>();
        if (isPreserveExistingBindings) {
            bindings = new ArrayList<>(iamClient.getIamPolicy(resourceName).getBindingsList());
        }

        bindings.add(
                Binding.newBuilder()
                        .setRole("roles/" + cloudConfig.getIamBindingRole())
                        .addMembers("serviceAccount:%s.svc.id.goog[%s/%s]".formatted(cloudConfig.getGcpProjectId(), k8sServiceAccountNamespace, k8sServiceAccountName))
                        .build()
        );

        Policy policy = Policy.newBuilder()
                .addAllBindings(bindings)
                .build();

        iamClient.setIamPolicy(resourceName, policy);
    }
}
