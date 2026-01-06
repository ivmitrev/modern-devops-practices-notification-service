#  Modern DevOps Practices - Notification Service

A production-grade Kotlin Notification Service demonstrating modern DevOps practices with fully automated CI/CD pipeline, security scanning, containerization, and Kubernetes deployment.

##  Table of Contents

- [Project Overview](#project-overview)
- [DevOps Topics Covered](#devops-topics-covered)
- [Technology Stack](#technology-stack)
- [Deep Dive: SAST Security Analysis](#deep-dive-sast-security-analysis)
- [Getting Started](#getting-started)
- [CI/CD Pipeline](#cicd-pipeline)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Additional Documentation](#additional-documentation)

##  Project Overview

**T-shaped DevOps implementation**: Comprehensive horizontal coverage of modern DevOps practices with a deep vertical dive into **SAST (Static Application Security Testing)**.

### Key Features
-  Multi-channel notifications (Email, Webhook)
-  Automated CI/CD with GitHub Actions
-  Multi-layered security (CodeQL, Detekt, Trivy)
-  Docker containerization with multi-stage builds
-  Kubernetes deployment with rolling updates
-  Database migrations with Flyway
-  Health checks and monitoring

##  DevOps Topics Covered

This project implements **12 DevOps topics** from the Modern DevOps Practices course:

| #  | Topic | Implementation | Location |
|----|-------|----------------|----------|
| 1  | **SDLC Phases** | Complete software development lifecycle | Entire project |
| 2  | **Collaborate** | Git-based workflow with issue tracking | GitHub, Pull Requests |
| 3  | **Source Control** | Git version control | `.git/`, `.gitignore` |
| 4  | **Branching Strategies** | GitHub Flow with feature branches | Branch protection rules |
| 5  | **Building Pipelines** | Multi-stage CI/CD automation | `.github/workflows/` |
| 6  | **Continuous Integration** | Automated build, test | `ci-cd.yaml` |
| 7  | **Continuous Delivery** | Automated Kubernetes deployment | `ci-cd.yaml` deployment job |
| 8  | **Security** | SAST, container scanning | `codeql.yaml`, `detekt.yaml`, Trivy |
| 9  | **Docker** | Multi-stage builds, optimization | `Dockerfile` |
| 10 | **Kubernetes** | Orchestration, services, configs | `k8s/` directory |
| 11 | **Infrastructure as Code** | Declarative K8s manifests | All YAML configs |
| 12 | **Database Changes** | Flyway migrations | `db/migration/` |

✅ **Mandatory Components**: Continuous Integration ✅ | Deploy to Kubernetes ✅

##  Technology Stack

**Core**: Kotlin • Spring Boot • Java 21 • Maven  
**Database**: H2 Database • Flyway Migrations  
**DevOps**: GitHub Actions • Docker • Kubernetes (Minikube)  
**Security**: CodeQL (SAST) • Detekt • Trivy  
**Testing**: JUnit 5 • MockK • Spring MockMvc

##  Architecture

```
GitHub Repository (Source Control)
         ↓
GitHub Actions CI/CD
  ├─ Build & Test (Unit tests, JUnit)
  ├─ Security Scan (CodeQL, Detekt)
  ├─ Docker Build & Scan (Trivy)
  └─ Deploy to Kubernetes (Minikube)
         ↓
Kubernetes Cluster
  ├─ Deployment (2 replicas)
  ├─ Service (NodePort)
  ├─ ConfigMap (Configuration)
  └─ Secrets (Credentials)
```

**Application Layers:**
```
REST API (NotificationController)
    ↓
Service Layer (NotificationService, EmailService, WebhookService)
    ↓
Repository Layer (NotificationRepository + H2 Database)
```

### Kubernetes Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster (Minikube)                    │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │           Namespace: notification-service                     │  │
│  │                                                               │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │ 
│  │  │  Service (NodePort)                                     │  │  │
│  │  │  - Type: NodePort                                       │  │  │
│  │  │  - Port: 80 → 8080                                      |  |  |
|  |  |  - NodePort: 30080                                      │  │  │
│  │  │  - Selector: app=notification-service                   │  │  │
│  │  └──────────────────────┬──────────────────────────────────┘  │  │
│  │                         │ (Routes traffic)                    │  │
│  │                         ↓                                     │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │
│  │  │  Deployment: notification-service                       │  │  │
│  │  │  - Replicas: 2                                          │  │  │
│  │  │                                                         │  │  │
│  │  │  ┌──────────────────────┐   ┌──────────────────────┐    │  │  │
│  │  │  │  Pod 1               │   │  Pod 2               │    │  │  │
│  │  │  │  ┌────────────────┐  │   │  ┌────────────────┐  │    │  │  │
│  │  │  │  │  Container     │  │   │  │  Container     │  │    │  │  │
│  │  │  │  │  notification- │  │   │  │  notification- │  │    │  │  │
│  │  │  │  │  service:latest│  │   │  │  service:latest│  │    │  │  │
│  │  │  │  │                │  │   │  │                │  │    │  │  │
│  │  │  │  │  Port: 8080    │  │   │  │  Port: 8080    │  │    │  │  │
│  │  │  │  │  User: spring  │  │   │  │  User: spring  │  │    │  │  │
│  │  │  │  └────────────────┘  │   │  └────────────────┘  │    │  │  │
│  │  │  │                      │   │                      │    │  │  │
│  │  │  │  Health Probes:      │   │  Health Probes:      │    │  │  │
│  │  │  │  ✓ Startup           │   │  ✓ Startup           │    │  │  │
│  │  │  │  ✓ Liveness          │   │  ✓ Liveness          │    │  │  │
│  │  │  │  ✓ Readiness         │   │  ✓ Readiness         │    │  │  │
│  │  │  └──────────┬───────────┘   └───────────┬──────────┘    │  │  │
│  │  │             │                           │               │  │  │
│  │  │             └───────────┬───────────────┘               │  │  │
│  │  │                         │ (Environment Variables)       │  │  │
│  │  └─────────────────────────┼───────────────────────────────┘  │  │
│  │                            │                                  │  │ 
│  │           ┌────────────────┴────────────────┐                 │  │
│  │           │                                 │                 │  │
│  │           ↓                                 ↓                 │  │
│  │  ┌────────────────┐              ┌─────────────────────┐      │  │
│  │  │  ConfigMap     │              │  Secret             │      │  │
│  │  │                │              │  (Created by CI/CD) │      │  │
│  │  │  - SPRING_     │              │                     │      │  │
│  │  │    PROFILES    │              │  - EMAIL_USERNAME   │      │  │
│  │  │  - DB_URL      │              │  - EMAIL_PASSWORD   │      │  │
│  │  │  - APP_CONFIG  │              │  - DB_PASSWORD      │      │  │
│  │  │                │              │                     │      │  │
│  │  └────────────────┘              └─────────────────────┘      │  │
│  │                                                               │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

##  Deep Dive: SAST Security Analysis

### What is SAST?
**Static Application Security Testing (SAST)** analyzes source code to detect security vulnerabilities before runtime. It's a critical "shift-left" security practice that catches issues early when they're cheapest to fix.

### Why SAST Matters
1. **Early Detection**: Finds vulnerabilities during development, not production
2. **Cost Effective**: 10-100x cheaper to fix than production bugs
3. **Compliance**: Required by PCI-DSS, HIPAA, SOC2
4. **Code Quality**: Improves maintainability and security awareness
5. **Automated**: Runs on every commit without manual intervention

### Three-Layer SAST Implementation

#### Layer 1: CodeQL (Semantic Analysis)
**Location**: `.github/workflows/codeql.yaml`

**What it Detects:**
- **Injection Vulnerabilities**: SQL injection, command injection, path traversal
- **Authentication Issues**: Broken auth, missing authorization checks
- **Data Exposure**: Sensitive data in logs, hardcoded credentials
- **Cryptography**: Weak algorithms, insecure random numbers
- **Code Quality**: Null pointers, resource leaks, dead code

**How it Works:**
1. Creates queryable database from source code
2. Runs semantic queries (security-and-quality suite)
3. Identifies vulnerability patterns
4. Generates SARIF reports
5. Integrates with GitHub Security tab

#### Layer 2: Detekt (Kotlin-Specific Analysis)
**Location**: `.github/workflows/detekt.yaml`

**What it Analyzes:**
- **Code Smells**: Complex methods, long parameters, large classes
- **Potential Bugs**: Unused code, unreachable code, exception handling
- **Style Violations**: Naming conventions, formatting, documentation
- **Performance**: Inefficient algorithms, memory leaks

**Rule Sets:**
- `complexity` - Cyclomatic complexity limits
- `exceptions` - Exception handling best practices
- `naming` - Kotlin naming conventions
- `performance` - Performance optimizations
- `potential-bugs` - Common bug patterns
- `style` - Code style guidelines

#### Layer 3: Trivy (Container Scanning)
**Location**: `ci-cd.yaml` (Docker build job)

**What it Scans:**
- **OS Vulnerabilities**: Alpine Linux packages, CVE database
- **Dependencies**: Java JARs, Maven dependencies (transitive)
- **Configurations**: Insecure Docker settings, exposed ports
- **Secrets**: Hardcoded credentials, API keys, tokens

**Scan Configuration:**
```yaml
Severity: CRITICAL, HIGH
Output: SARIF
Database: NVD, Red Hat, Debian, Alpine Security
```

##  Getting Started

### Prerequisites
- Java 21 (OpenJDK/Temurin)
- Maven 3.9+ (or use wrapper)
- Docker
- kubectl + Minikube (optional)

### Quick Start

**1. Clone & Configure**
```bash
git clone https://github.com/ivmitrev/modern-devops-practices-notification-service.git
cd modern-devops-practices-notification-service

# Setup credentials (use https://ethereal.email/ for testing)
export EMAIL_USERNAME="your-email@example.com"
export EMAIL_PASSWORD="your-password"
```

**2. Build & Test**
```bash
./mvnw clean install
./mvnw test
```

**3. Run Locally** (Choose one option)

**Option A: Maven**
```bash
./mvnw spring-boot:run
# Access: http://localhost:8080
```

**Option B: Docker Compose**
```bash
docker-compose up --build
```

**Option C: Kubernetes**
```bash
minikube start
docker build -t notification-service:latest .
minikube image load notification-service:latest

kubectl create namespace notification-service
kubectl create secret generic notification-service-secret \
  --namespace=notification-service \
  --from-literal=EMAIL_USERNAME="${EMAIL_USERNAME}" \
  --from-literal=EMAIL_PASSWORD="${EMAIL_PASSWORD}" \
  --from-literal=SPRING_DATASOURCE_PASSWORD=""

kubectl apply -f k8s/
minikube service notification-service -n notification-service
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| **POST** | `/api/notifications/send` | Create and send new notification |
| **GET** | `/api/notifications/{id}` | Retrieve specific notification by ID |
| **GET** | `/api/notifications/user/{userId}` | Retrieve all notifications for a user |
| **GET** | `/api/notifications` | Retrieve all notifications |

**Create Email Notification**
```bash
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "EMAIL",
    "recipient": "test@example.com",
    "subject": "Test",
    "message": "Hello!"
  }'
```

**Create Webhook Notification**
```bash
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user456",
    "type": "WEBHOOK",
    "recipient": "https://webhook.site/your-id",
    "message": "Webhook test"
  }'
```

**Health Check**
```bash
curl http://localhost:8080/actuator/health
```

##  CI/CD Pipeline

### Pipeline Jobs

**Job 1: Build & Test**
- Checkout code
- Setup JDK 21 + Maven cache
- Run `mvn clean verify` (compile, test, package)
- Success: All tests pass, no errors

**Job 2: Docker Build & Scan**
- Setup Docker Buildx
- Multi-stage build
- Trivy vulnerability scan (CRITICAL/HIGH)
- Save image artifact

**Job 3: Deploy to Kubernetes**
- Start Minikube cluster
- Load Docker image
- Apply K8s resources (namespace, secrets, configmap, deployment, service)
- Verify rollout + health check

**Parallel: Security Scans**
- CodeQL Analysis: Java/Kotlin security analysis
- Detekt Scan: Kotlin code quality

### Triggers
- **Pull Request → main**: All CI checks
- **Push → main**: Full CI/CD + deployment
- **Schedule**: Periodic security scans

### Secrets Required
```yaml
GitHub Repository → Settings → Secrets
- EMAIL_USERNAME
- EMAIL_PASSWORD
```

##  Kubernetes Deployment

### Resources Overview

| Resource | File | Purpose                    |
|----------|------|----------------------------|
| Namespace | `namespace.yaml` | Isolation                  |
| ConfigMap | `configmap.yaml` | Non-sensitive config       |
| Secret | `secret.yaml` | Credentials (example file) |
| Deployment | `deployment.yaml` | 2 replicas + health probes |
| Service | `service.yaml` | NodePort               |

### Common Commands

```bash
# Deploy all
kubectl apply -f k8s/

# Check status
kubectl get all -n notification-service
kubectl rollout status deployment/notification-service -n notification-service

# View logs
kubectl logs -l app=notification-service -n notification-service -f

# Scale
kubectl scale deployment/notification-service --replicas=3 -n notification-service

# Rollback
kubectl rollout undo deployment/notification-service -n notification-service

# Debug
kubectl describe pod <pod-name> -n notification-service
kubectl get events -n notification-service --sort-by='.lastTimestamp'
```

##  Database Migrations

**Flyway** manages database schema versions automatically.

**Current Schema**: `V1__create_notifications_table.sql`
```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP,
    error TEXT
);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
```

**Add New Migration:**
```bash
# Create file: src/main/resources/db/migration/V2__description.sql
# Flyway runs automatically on startup
./mvnw flyway:info      # View migration status
./mvnw flyway:validate  # Validate migrations
```

##  Testing Strategy

**Run Tests:**
```bash
./mvnw test          # Unit tests
./mvnw verify        # All tests
```

**Frameworks:** JUnit 5, MockK, Spring MockMvc, MockMvc

##  Branching Strategy

**GitHub Flow** with protected main branch

```
main (protected)
  ├─ feature/add-sms
  ├─ fix/fix-timeout
```

**Workflow:**
1. Create issue
2. Branch: `feature/add-sms`
3. Develop + commit: `feat: Added sms notifications`
4. Push + create PR
5. CI/CD checks + code review
6. Squash and merge

##  Monitoring & Health

**Actuator Endpoints:**
```bash
/actuator/health            # Overall health
/actuator/health/liveness   # Liveness probe
/actuator/health/readiness  # Readiness probe
/actuator/info              # App info
/actuator/metrics           # Metrics
```

**Kubernetes Monitoring:**
```bash
kubectl get pods -n notification-service
kubectl top pods -n notification-service
kubectl logs -f deployment/notification-service -n notification-service
```

##  Additional Documentation

### Project Structure
```
.github/workflows/     # CI/CD pipelines
k8s/                   # Kubernetes manifests
src/main/kotlin/       # Application code
src/main/resources/    # Configs + DB migrations
src/test/kotlin/       # Unit tests
Dockerfile             # Multi-stage container build
docker-compose.yml     # Local development
pom.xml                # Maven dependencies
```

### Security Reports
All security findings: `GitHub → Security → Code Scanning`
- CodeQL: Security vulnerabilities
- Detekt: Code quality issues
- Trivy: Container vulnerabilities

### Future Enhancements
- [ ] SMS/Push notifications
- [ ] PostgreSQL
- [ ] Message queue (RabbitMQ/Kafka)
- [ ] OAuth2 authentication
