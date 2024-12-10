[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/woco-io)](https://artifacthub.io/packages/search?repo=woco-io)

# cloud-iam-manager Helm Chart

This chart installs cloud-iam-manager deployment.<br>
<br>
This Helm chart simplifies the process of binding Google Service Accounts (GSA) to Kubernetes Service Accounts (KSA) in a Kubernetes environment, particularly when using Google Kubernetes Engine (GKE). The service automatically handles the necessary IAM binding between GSAs and KSAs.

## Features

- Automatically binds GSAs to KSAs using the specified GKE annotation.
- Configurable to work in a specific Kubernetes namespace.
- Supports Google Cloud Platform (GCP) and integrates with IAM roles.
- Configurable resource limits, liveness, readiness probes, and more.

## Chart Details

- **Chart Name**: `cloud-iam-manager`
- **Version**: `1.0.0`
- **Application Version**: `1.0.0`
- **Home**: [woco-io](https://github.com/woco-io)
- **Sources**: [GitHub Repository](https://github.com/woco-io/cloud-iam-manager)
- **Maintainer**: Yehoraz Levi ([email](mailto:yehoraz3@gmail.com), [GitHub](https://github.com/Yehoraz))

## Installation

### Prerequisites

- A Kubernetes cluster.
- Helm 3.x installed.
- Terraform (optional).

### Setup Required Infrastructure

this can be replaced with manual / gcloud commands as long as the GSA has the required permissions and is bound to the KSA.

```terraform
# Create a Google Service Account
resource "google_service_account" "k8s_service_account" {
  account_id   = "<your-service-account-id>" # Replace with your desired service account id
  display_name = "Kubernetes Service Account for Binding"
}

# Assign the specified IAM roles to the Google Service Account
resource "google_project_iam_member" "iam_bindings" {
  for_each = toset([
    "roles/iam.serviceAccount.get",
    "roles/iam.serviceAccount.getIamPolicy",
    "roles/iam.serviceAccount.list",
    "roles/iam.serviceAccount.setIamPolicy"
  ])

  project = "<your-gcp-project-id>"  # Replace with your GCP project ID
  role    = each.value
  member  = "serviceAccount:${google_service_account.k8s_service_account.email}"
}

# Bind Google Service Account to Kubernetes Service Account 
resource "google_service_account_iam_member" "bind_gsa_to_ksa" {
  member             = "serviceAccount:<your-gcp-project-id>.svc.id.goog[<your-namespace>/<your-k8s-service-account>]" # Replace with your GCP project ID, namespace, ksa
  role               = "roles/iam.workloadIdentityUser"
  service_account_id = google_service_account.k8s_service_account.id
}
```

### Setup Repo Info

```console
helm repo add woco-io https://woco-io.github.io/helm-charts/
helm repo update
```

_See [helm repo](https://helm.sh/docs/helm/helm_repo/) for command documentation._


### Install the Chart

To install the `cloud-iam-manager` chart, use the following Helm command:

```bash
kubectl create namespace <your-namespace>
kubectl label namespace <your-namespace> cloud-iam-manager.woco.io/enabled=true --overwrite
helm install my-cloud-iam-manager woco-io/cloud-iam-manager --version <chart-chosen-version> --namespace <your-namespace>
```

### Uninstalling the Chart

To uninstall/delete the `my-cloud-iam-manager` deployment:

```console
helm delete my-cloud-iam-manager --namespace <your-namespace>
```

For more details on configuration options, refer to the section below.

## Values Configuration

### Available Values

| Key                            | Description                                                                                     | Default Value                |
|--------------------------------|-------------------------------------------------------------------------------------------------|------------------------------|
| `isAutomaticPermissions`       | Whether to automatically create service permissions (RBAC) and Kubernetes Service Account (KSA) | `true`                       |
| `app.name`                     | Name of the application.                                                                        | `cloud-iam-manager`            |
| `app.image.name`               | Docker image name for the service.                                                              | `woco.io/cloud-iam-manager`    |
| `app.image.tag`                | Docker image tag.                                                                               | `1.0`                        |
| `app.image.pullPolicy`         | Image pull policy.                                                                              | `IfNotPresent`               |
| `app.port`                     | Port for the service.                                                                           | `5000`                       |
| `app.env`                      | Environment variables for the service. See `Environment Variables` section for more details.    | `{}`                         |
| `labels`                       | Labels for the resources created by this chart.                                                 | `{"app": "cloud-iam-manager"}` |
| `annotations`                  | Annotations for the resources created by this chart.                                            | `{"app": "cloud-iam-manager"}` |
| `strategy`                     | Deployment strategy. Configure rolling updates and other settings.                              | `RollingUpdate`              |
| `strategy.maxSurge`            | Maximum number of pods to be created above the desired number during a rolling update.          | `25%`                        |
| `strategy.maxUnavailable`      | Maximum number of pods that can be unavailable during a rolling update.                         | `25%`                        |
| `replicas`                     | Number of replicas for the service.                                                             | `1`                          |
| `nodeSelector`                 | Node selector for pod scheduling.                                                               | `{}`                         |
| `affinity`                     | Affinity rules for pod scheduling.                                                              | `{}`                         |
| `tolerations`                  | Tolerations for pod scheduling.                                                                 | `{}`                         |
| `imagePullSecrets`             | Secrets to use for pulling images from private registries.                                      | `[]`                         |
| `liveness`                     | Configurations for liveness probes. See `liveness` section for more details.                    | `{}`                         |
| `readiness`                    | Configurations for readiness probes. See `readiness` section for more details.                  | `{}`                         |
| `resources`                    | Resource requests and limits for the service. See `resources` section for more details.         | `{}`                         |
| `serviceAccountName`           | Name of the service account to be used by the pod.                                              | `""`                         |
| `gcpServiceAccount`            | Google Cloud Service Account to be used for API calls against GCP.                              | Required                     |
| `gcpProjectId`                 | Google Cloud project id this application is deployed in.                                        | Required                     |
| `createNamespace`              | Whether to create namespace for the service.                                                    | `false`                      |

---

### Environment Variables

The `cloud-iam-manager` service can be configured through environment variables. These environment variables override the corresponding values in the `application.yaml` configuration file. Below are the environment variables that can be set:

| Environment Variable                            | Description                                                                                             | Default Value                          |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------|----------------------------------------|
| `APP_SERVER_PORT`                               | Port on which the service will run.                                                                     | `5000`                                 |
| `APP_HEALTH_LIVE_INCLUDE_K8S_CLIENT`            | Include custom Kubernetes client liveness checks.                                                       | `true`                                 |
| `APP_CLOUD_IAM_MANAGER_NS_LABEL`                  | Label used to filter Kubernetes namespaces for the service to operate in, use "all" for all namespaces. | `cloud-iam-manager.woco.io/enabled=true` |
| `APP_CLOUD_IAM_MANAGER_EVENT_TIMING_MINUS_HOURS`  | Adjust event timing (in hours) for the service.                                                         | `0`                                    |
| `APP_CLOUD_IAM_MANAGER_SA_IAM_ANNOTATION`         | Annotation key used to identify the Kubernetes Service Accounts to bind.                                | `iam.gke.io/gcp-service-account`       |
| `APP_CLOUD_IAM_MANAGER_CLOUD_PROVIDER`            | The cloud provider (supports GCP).                                                                      | `GCP`                                  |
| `APP_CLOUD_IAM_MANAGER_IAM_BINDING_ROLE`          | IAM role used when binding the Google Service Account to the Kubernetes Service Account.                | `iam.workloadIdentityUser`             |
| `APP_GCP_PROJECT_ID`                            | Google Cloud Project ID to be used for the GSA binding.                                                 | `placeholder-fake-value`               |
| `APP_CLOUD_IAM_MANAGER_IS_PRESERVE_IAM_BINDINGS`  | Whether to preserve IAM bindings.                                                                       | `true`                                 |
| `APP_IS_USE_CACHE`                              | Whether to enable cache usage.                                                                          | `true`                                 |

---

### Custom Values

You can override the default values in the `values.yaml` file during installation by passing custom values via the `--set` flag or by providing a custom values file.

For example:

```bash
helm install my-cloud-iam-manager woco-io/cloud-iam-manager --version <chart-chosen-version> --namespace <your-namespace> --set app.name=my-app,app.image.tag=latest
```

## Notes

- The service requires the `APP_GCP_PROJECT_ID` to be set for GCP integration.
- Ensure your Kubernetes cluster is running on GKE to take full advantage of the GSA <-> KSA binding features.
- You can customize resource requests and limits in the `values.yaml` file to suit your deployment needs.


## Values Schema

This chart uses a values schema to enforce valid configuration. The schema can be found in the `values.schema.json` file, which includes detailed descriptions of all configurable properties.

You can validate your values by running:

```bash
helm pull woco-io/cloud-iam-manager --version <chart-chosen-version> --untar
helm lint cloud-iam-manager --version <chart-chosen-version> --namespace <your-namespace> --set app.name=my-app,app.image.tag=latest
```

## Conclusion

The `cloud-iam-manager` Helm chart simplifies the management of Google Kubernetes Engine (GKE) workloads by automating the process of binding Google Service Accounts to Kubernetes Service Accounts. This chart provides a customizable deployment solution that integrates easily with GCP IAM and Kubernetes RBAC.
