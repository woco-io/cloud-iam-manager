[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/woco-io)](https://artifacthub.io/packages/search?repo=woco-io)

# cloud-iam-manager

The `cloud-iam-manager` service is designed to run within a Kubernetes cluster and facilitates the binding of Google Cloud Service Accounts (GSA) to Kubernetes Service Accounts (KSA) using a custom annotation. This service automatically identifies Kubernetes Service Accounts with specific annotations and uses the annotation value to create bindings between the GKE Service Account and the Kubernetes Service Account.

## Features
- Identifies Kubernetes Service Accounts with the specified Google Cloud Service Account annotation.
- Binds Google Service Accounts to Kubernetes Service Accounts for seamless GKE workload integration.
- Configurable through environment variables and `application.yaml` file.
- Health checks and detailed logging support for Kubernetes monitoring.

## Prerequisites
Before deploying the service, ensure the following are available:
1. A running Kubernetes cluster.
2. Helm installed and configured to deploy charts to the cluster.
3. Docker installed for building the Docker image.
4. A Google Cloud project with the necessary IAM permissions for managing service accounts.

## Installation

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/woco-io/cloud-iam-manager.git
cd cloud-iam-manager
```

### 2. Build the Docker Image

Ensure Docker is installed. Build the Docker image with the following command:

```bash
docker build -t cloud-iam-manager .
```

### 3. Package the Helm Chart

Package the Helm chart for deployment:

```bash
helm package ./helm
```

This will generate a `.tgz` file that you can deploy to your Kubernetes cluster.

### 4. Deploy Using Helm

Deploy the `cloud-iam-manager` service to your Kubernetes cluster with:

```bash
helm install cloud-iam-manager ./cloud-iam-manager-<version>.tgz
```

Replace `<version>` with the version of the chart.

## Configuration

The service can be configured using the `application.yaml` file or through environment variables. Here are the main configuration settings:

### Application Configuration

```yaml
server:
  port: ${APP_SERVER_PORT:5000}

app-conf:
  health:
    liveness:
      k8s-client:
        include: ${APP_HEALTH_LIVE_INCLUDE_K8S_CLIENT:true}
  runners:
    polling-interval: ${APP_RUNNERS_POLLING_INTERVAL:10000}
  k8s-config:
    namespace-label: ${APP_CLOUD_IAM_MANAGER_NS_LABEL:cloud-iam-manager.woco.io/enabled=true}
    eventTimingMinusHours: ${APP_CLOUD_IAM_MANAGER_EVENT_TIMING_MINUS_HOURS:0}
    service-account-iam-annotation: ${APP_CLOUD_IAM_MANAGER_SA_IAM_ANNOTATION:iam.gke.io/gcp-service-account}
  cloud-config:
    cloud-provider: ${APP_CLOUD_IAM_MANAGER_CLOUD_PROVIDER:GCP}
    iam-binding-role: ${APP_CLOUD_IAM_MANAGER_IAM_BINDING_ROLE:iam.workloadIdentityUser}
    project-id: ${APP_GCP_PROJECT_ID}
    is-preserve-iam-bindings: ${APP_CLOUD_IAM_MANAGER_IS_PRESERVE_IAM_BINDINGS:true}
  global:
    is-use-cache: ${APP_IS_USE_CACHE:true}
```

- `port`: Port on which the service will run.
- `namespace-label`: Label used to identify the Kubernetes namespace where the service will operate.
- `service-account-iam-annotation`: Annotation key used to identify the Kubernetes Service Accounts to bind.
- `cloud-provider`: The cloud provider, set to GCP.
- `iam-binding-role`: IAM role used when binding the Google Service Account to the Kubernetes Service Account.
- `project-id`: Google Cloud Project ID.

### Health and Logging

The application includes health endpoints and configurable logging levels:

```yaml
management:
  endpoint:
    health:
      group:
        liveness:
          include: livenessState, k8sClientCustomLivenessIndicator
      show-components: always
      show-details: always
      probes:
        enabled: true

spring:
  application:
    name: cloud-iam-manager

logging:
  level:
    root: ${ROOT_LOG_LEVEL:INFO}
    io:
      woco:
        cloud_iam_manager: ${APP_LOG_LEVEL:INFO}
```

- Health probes are available for liveness and custom indicators.
- Detailed logging can be configured for both root and service-specific logs.

---

### Environment Variables

The `cloud-iam-manager` service can be configured through environment variables. These environment variables override the corresponding values in the `application.yaml` configuration file. Below are the environment variables that can be set:

| Environment Variable                             | Description                                                                                             | Default Value                            |
|--------------------------------------------------|---------------------------------------------------------------------------------------------------------|------------------------------------------|
| `APP_SERVER_PORT`                                | Port on which the service will run.                                                                     | `5000`                                   |
| `APP_HEALTH_LIVE_INCLUDE_K8S_CLIENT`             | Include custom Kubernetes client liveness checks.                                                       | `true`                                   |
| `APP_RUNNERS_POLLING_INTERVAL`                   | Polling runner interval.                                                                                | `10000`                                  |
| `APP_CLOUD_IAM_MANAGER_NS_LABEL`                 | Label used to filter Kubernetes namespaces for the service to operate in, use "all" for all namespaces. | `cloud-iam-manager.woco.io/enabled=true` |
| `APP_CLOUD_IAM_MANAGER_EVENT_TIMING_MINUS_HOURS` | Adjust event timing (in hours) for the service.                                                         | `0`                                      |
| `APP_CLOUD_IAM_MANAGER_SA_IAM_ANNOTATION`        | Annotation key used to identify the Kubernetes Service Accounts to bind.                                | `iam.gke.io/gcp-service-account`         |
| `APP_CLOUD_IAM_MANAGER_CLOUD_PROVIDER`           | The cloud provider (supports GCP).                                                                      | `GCP`                                    |
| `APP_CLOUD_IAM_MANAGER_IAM_BINDING_ROLE`         | IAM role used when binding the Google Service Account to the Kubernetes Service Account.                | `iam.workloadIdentityUser`               |
| `APP_GCP_PROJECT_ID`                             | Google Cloud Project ID to be used for the GSA binding.                                                 | null                                     |
| `APP_CLOUD_IAM_MANAGER_IS_PRESERVE_IAM_BINDINGS` | Whether to preserve IAM bindings.                                                                       | `true`                                   |
| `APP_IS_USE_CACHE`                               | Whether to enable cache usage.                                                                          | `true`                                   |

---

## How It Works

1. **Kubernetes Service Account Detection:** The service scans the cluster for Kubernetes Service Accounts with the specified annotation key (`iam.gke.io/gcp-service-account`).
2. **Google Service Account Binding:** The value of the annotation (GSA email) is used to create a binding between the Kubernetes Service Account and the Google Service Account.
3. **GCP Workload Access:** Workloads using the KSA can access Google Cloud resources, based on the IAM roles and permissions of the associated GSA.

## Example Usage

Create a Kubernetes Service Account with the required annotation:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-service-account
  annotations:
    iam.gke.io/gcp-service-account: "my-gsa@my-project.iam.gserviceaccount.com"
```

Deploy workloads with the Service Account to enable GCP resource access via the bound Google Service Account.

## Troubleshooting

If binding does not occur:
- Ensure cloud-iam-manager service account is bound to a valid GSA and has enough permissions to run the "bind" api.
- Ensure the Kubernetes Service Account has the correct annotation.
- Verify that the Google Service Account exists and has appropriate IAM roles.
- Review the service logs using `kubectl logs` for any errors or issues during the binding process.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
