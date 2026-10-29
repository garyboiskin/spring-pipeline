# spring-pipeline

A minimal Spring Boot REST API sample that handles GET, POST, PUT, PATCH, and
DELETE requests at `/api/requests`. POST, PUT, and PATCH accept a JSON object;
each response reports the HTTP method and echoes the request body.

Run the application with Java 17 and Maven:

#Test dev branch

```sh
mvn spring-boot:run
```

Run with Docker:

```sh
docker build -t spring-pipeline .
docker run --rm -p 8088:8080 spring-pipeline
```

For example
```sh
curl http://localhost:8080/api/requests
curl -X POST http://localhost:8080/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"sample"}'
curl -X PUT http://localhost:8080/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"updated"}'
curl -X PATCH http://localhost:8080/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"changed"}'
curl -X DELETE http://localhost:8080/api/requests

mvn test
```

## Kubernetes

Manifests are in `k8s/` (Deployment with 2 replicas and a NodePort Service on
port 30080). The app listens on port 8080 inside the container.

```sh
docker build -t spring-pipeline:latest .
kubectl apply -f k8s/
kubectl get pods -l app=spring-pipeline
kubectl port-forward svc/spring-pipeline 30080:8080
curl http://localhost:30080/api/requests
```

With minikube, run `minikube image load spring-pipeline:latest` first and use
`minikube service spring-pipeline --url` to get the URL.

## Jenkins PR pipeline (local Jenkins)

This repository includes a `Jenkinsfile` at the project root. To run pull
request builds from a local Jenkins service (`brew services start jenkins-lts`):

1. In Jenkins, install plugins: `Pipeline`, `GitHub Branch Source`, and `Git`.
2. Add GitHub credentials (a Personal Access Token with repo read access).
3. Create a **Multibranch Pipeline** job.
4. In Branch Sources, add GitHub repository:
   `https://github.com/garyboiskin/spring-pipeline`.
5. Configure pull request discovery strategy (for example, merge PR with target
   branch before build).
6. Set Scan Multibranch Pipeline Triggers to periodically scan (for example,
   every 5 minutes) if webhook delivery to local Jenkins is not available.

Pipeline behavior:
- runs `mvn -B clean verify` on each branch/PR build,
- archives JAR artifacts from `target/*.jar`,
- publishes JUnit reports from `target/surefire-reports/*.xml`.

