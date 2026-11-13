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
docker run --rm -p 8081:8080 spring-pipeline
```

For example
```sh
curl http://localhost:8081/api/requests
curl -X POST http://localhost:8081/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"sample"}'
curl -X PUT http://localhost:8081/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"updated"}'
curl -X PATCH http://localhost:8081/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"changed"}'
curl -X DELETE http://localhost:8081/api/requests

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

AWS ECR  image  URI for the app
839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:latest
AWS ECR  image tag   URI for the app latest
839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks@sha256:b1013bc4ee920427e7c3e04f03c499eb8bf3d2ad9069ec022d55483202678139
AWS ECR  image tag   URI for the app with size 0
839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks@sha256:2745aac1f2de5467d80a654191df88658054126a60d63167067ca4d2e735cd85

eksctl create cluster \
--name=spring-boot-cluster \
--region=us-east-2 \
--version=1.28 \
--node-type=t2.small \

eksctl create cluster --name spring-boot-cluster --region us-east-2 --version 1.28 --node-type=t2.small

eksctl create cluster --name spring-boot-cluster --region us-east-2 --node-type t3.medium --nodes 2

eksctl create cluster --name spring-boot-cluster --region us-east-2 --node-type m7i-flex.large --nodes 2  --managed

# eksctl create cluster --name spring-boot-cluster --region us-east-2 --node-type 
m7i-flex.large
8 GiB
2

eksctl create cluster --name spring-boot-cluster --region us-east-2 --node-type m7i-flex.large --nodes 2


connect local k8 to cluster
aws eks update-kubeconfig --name spring-boot-cluster --region us-east-2 


aws iam put-user-policy --user-name GaryIAM --policy-name EksFullAccess --policy-document '{
"Version": "2012-10-17",
"Statement": [
{ "Effect": "Allow", "Action": "eks:*", "Resource": "*" }
]
}'


garyboriskin@Garys-MacBook-Air spring-pipeline % aws sts get-caller-identity
{
"UserId": "AIDA4HACE5SXEFBYAEOCU",
"Account": "839670623406",
"Arn": "arn:aws:iam::839670623406:user/GaryIAM"
}


PRINCIPAL=arn:aws:iam::839670623406:user/GaryIAM   # replace with your console ARN

aws eks create-access-entry --cluster-name spring-boot-cluster --region us-east-2  --principal-arn "arn:aws:iam::839670623406:user/GaryIAM"

aws eks associate-access-policy --cluster-name spring-boot-cluster --region us-east-2 \
--principal-arn "$PRINCIPAL" \
--policy-arn arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy \
--access-scope type=cluster

command to apply the deployment and service manifests to the cluster
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl get svc
kubectl get pods

ImagePullBackOff with "no match for platform in manifest" (image built on Apple Silicon
is arm64, EKS nodes are amd64). Build and push for linux/amd64, then restart the pods:
docker buildx build --platform linux/amd64 -t 839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:latest --push .
kubectl rollout restart deployment/spring-pipeline

external ip
k8s-default-springpi-7fd6388d3a-1be25d346e6b7dee.elb.us-east-2.amazonaws.com

839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks

List the images in the repository:
aws ecr list-images --repository-name springboot-eks --region us-east-2

A more readable table with tags, push date, size and digest:

aws ecr describe-images --repository-name springboot-eks --region us-east-2 \
--query 'sort_by(imageDetails,&imagePushedAt)[].{Tags:imageTags,Pushed:imagePushedAt,SizeMB:imageSizeInBytes,Digest:imageDigest}' \
--output table

# Only tag names
aws ecr list-images --repository-name springboot-eks --region us-east-2

# Check which architecture an image is (arm64 vs amd64)
docker buildx imagetools inspect 839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:latest

# Show the most recently pushed image
aws ecr describe-images --repository-name springboot-eks --region us-east-2 \
--query 'sort_by(imageDetails,&imagePushedAt)[-1]'

Delete all images (keeps the empty repository):

aws ecr batch-delete-image --repository-name springboot-eks --region us-east-2 \
--image-ids "$(aws ecr list-images --repository-name springboot-eks --region us-east-2 --query 'imageIds[*]' --output json)"

Two steps per stack: turn off termination protection, then delete. Delete the node group stack first, because the cluster stack's resources are in use until it's gone.

# 1. Node group stack
aws cloudformation update-termination-protection --no-enable-termination-protection \
--region us-east-2 --stack-name eksctl-spring-boot-cluster-nodegroup-ng-32c8a0c2
aws cloudformation delete-stack \
--region us-east-2 --stack-name eksctl-spring-boot-cluster-nodegroup-ng-32c8a0c2
aws cloudformation wait stack-delete-complete \
--region us-east-2 --stack-name eksctl-spring-boot-cluster-nodegroup-ng-32c8a0c2

# 2. Cluster stack
aws cloudformation update-termination-protection --no-enable-termination-protection \
--region us-east-2 --stack-name eksctl-spring-boot-cluster-cluster
aws cloudformation delete-stack \
--region us-east-2 --stack-name eksctl-spring-boot-cluster-cluster
aws cloudformation wait stack-delete-complete \
--region us-east-2 --stack-name eksctl-spring-boot-cluster-cluster

Check the result:
aws cloudformation list-stacks --region us-east-2 \
--stack-status-filter CREATE_COMPLETE ROLLBACK_COMPLETE DELETE_FAILED \
--query 'StackSummaries[?contains(StackName,`spring-boot-cluster`)].[StackName,StackStatus]' --output text


An empty result means both are deleted.

Console alternative: go to CloudFormation → Stacks → select the stack → Stack actions → Edit termination protection → Deactivated → Save. Then choose Delete.

curl http://a6746f810013843c0b78964fe860f43b-1703028323.us-east-2.elb.amazonaws.com/api/requests

The address is the hostname of the load balancer that Kubernetes created for your Service. It shows up in the EXTERNAL-IP column:

To build the full URL in one step:

echo "http://$(kubectl get svc spring-pipeline -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')/api/requests"

environment {
MAVEN_OPTS     = '-Dmaven.repo.local=.m2/repository'
AWS_REGION     = 'us-east-1'                 // your region
AWS_ACCOUNT_ID = '123456789012'              // your account
CLUSTER_NAME   = 'spring-boot-cluster'
ECR_REPO       = 'spring-pipeline'
IMAGE          = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${BUILD_NUMBER}"
}
...
stage('Deploy') {
when { anyOf { branch 'main'; branch 'master' } }
steps {
withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
credentialsId: 'aws-creds']]) {
sh '''
aws ecr describe-repositories --repository-names $ECR_REPO --region $AWS_REGION \
|| aws ecr create-repository --repository-name $ECR_REPO --region $AWS_REGION
aws ecr get-login-password --region $AWS_REGION | \
docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
docker build -t $IMAGE .
docker push $IMAGE

                aws eks update-kubeconfig --name $CLUSTER_NAME --region $AWS_REGION
                kubectl apply -f k8s/
                kubectl set image deployment/spring-pipeline spring-pipeline=$IMAGE
                kubectl rollout status deployment/spring-pipeline --timeout=180s
            '''
        }
    }
}

#find account id 
garyboriskin@Garys-MacBook-Air spring-pipeline % aws sts get-caller-identity --query Account --output text
839670623406

environment {
MAVEN_OPTS     = '-Dmaven.repo.local=.m2/repository'
AWS_REGION     = 'us-east-1'                 // your region
AWS_ACCOUNT_ID = '839670623406'              // your account
CLUSTER_NAME   = 'spring-boot-cluster'
ECR_REPO       = 'spring-pipeline'
IMAGE          = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${BUILD_NUMBER}"
}

Created  aws kiy and secret key for Jenkins credentials and added to Jenkinsfile.  Added AWS_REGION, AWS_ACCOUNT_ID, CLUSTER_NAME, ECR_REPO, IMAGE to Jenkinsfile. 
Added withCredentials block to Deploy stage in Jenkinsfile.
aryboriskin@Garys-MacBook-Air spring-pipeline % kubectl describe pod spring-pipeline-f94457bd-dkfsm  | tail -20   
Volumes:  
kube-api-access-f4cjn:  
Type:                    Projected (a volume that contains injected data from multiple sources)  
TokenExpirationSeconds:  3607  
ConfigMapName:           kube-root-ca.crt  
Optional:                false  
DownwardAPI:             true  
QoS Class:                   Burstable  
Node-Selectors:              <none>  
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s  
node.kubernetes.io/unreachable:NoExecute op=Exists for 300s  
Events:  
Type     Reason     Age                From               Message
  ----     ------     ----               ----               -------  
Normal   Scheduled  96s                default-scheduler  Successfully assigned default/spring-pipeline-f94457bd-dkfsm to ip-192-168-51-105.us-east-2.compute.internal  
Normal   BackOff    15s (x5 over 95s)  kubelet            Back-off pulling image "839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:18"  
Warning  Failed     15s (x5 over 95s)  kubelet            Error: ImagePullBackOff  
Normal   Pulling    4s (x4 over 95s)   kubelet            Pulling image "839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:18"  
Warning  Failed     4s (x4 over 95s)   kubelet            Failed to pull image "839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:18": rpc error: code = NotFound desc = failed to pull and unpack image "839670623406.dkr.ecr.us-east-2.amazonaws.com/springboot-eks:18": no match for platform in manifest: not found  
Warning  Failed     4s (x4 over 95s)   kubelet            Error: ErrImagePull  

The pull itself works now. The new error, no match for platform in manifest, means the image architecture doesn't match the nodes. Jenkins is running on your Apple Silicon Mac, so it builds an arm64 image, but the EKS nodes are amd64. I'll build for linux/amd64.

Your Service is type: LoadBalancer, so AWS gives it a DNS name.
kubectl
kubectl get svc spring-pipeline
he EXTERNAL-IP column shows a hostname like xxxx.elb.us-east-2.amazonaws.com. If it says <pending>, wait a minute or two and run it again.
Print just the hostname:
kubectl get svc spring-pipeline -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'

mvn -B verify -Dcoverage.check.skip=false 2>&1 | grep -E "^\[(INFO|ERROR|WARNING)\].*(Tests run:|coverage|Coverage|BUILD|covered ratio|Rule)" | head; java -version 2>&1 | head -1


mvn -B verify -Dcoverage.check.skip=false 2>&1 | grep -E "Tests run:|jacoco|coverage|BUILD|ERROR" | head -15

mvn -B verify -Dcoverage.check.skip=false 2>&1 | grep -E "^\[(INFO|ERROR|WARNING)\].*(Tests run:|Rule|BUILD|All coverage)"

mvn -B verify -Dcoverage.check.skip=false 2>&1 | grep -E "Rule|BUILD|coverage checks"; mvn -B -q verify 2>&1 | grep -E "BUILD|ERROR" | head -3; echo default-ok

gh pr list --state all --limit 5 --json number,baseRefName,headRefOid,title; gh api repos/garyboiskin/spring-pipeline/branches --jq '.[].name'

curl -s -m 10 http://localhost:8080/job/springboot-pipeline-to-github/job/dev/20/consoleText | grep -E "Coverage|coverage|BUILD|Rule|Finished|Stage|Tests run:" | tail -15; gh pr list --state all --limit 3 --json number,baseRefName,headRefName,headRefOid

mvn -B verify -Dcoverage.check.skip=false

mvn -B verify -Dcoverage.check.skip=false 2>&1 | grep -E "Rule|BUILD|coverage checks"

Use CloudWatch Logs (and optionally kubectl) to view EKS app logs.
1.
If using kubectl directly (fastest):
kubectl logs -f deployment/spring-pipeline -n default
For a specific pod:
kubectl get pods -n default -l app=spring-pipeline
kubectl logs -f <pod-name> -n default
2.
In AWS Console (CloudWatch):
1.
Open CloudWatch → Logs → Log groups.
2.
Find your EKS/container log group (often under /aws/containerinsights/<cluster-name>/... if Container Insights/Fluent Bit is enabled).
3.
Open a log stream for the spring-pipeline pod/container.



Use Amazon RDS (PostgreSQL) and connect your app via JDBC.

DB name: postgresDB
username: postgres
password: password
1.
Create DB (Console): RDS → Create database → PostgreSQL → choose credentials, DB name, same VPC as EKS, private access, and allow inbound 5432 from your EKS node/pod security group.
2.
Get endpoint: after creation, copy Endpoint and port.
3.
Store secrets in Kubernetes:
kubectl create secret generic db-secret \
--from-literal=SPRING_DATASOURCE_USERNAME='postgres' \
--from-literal=SPRING_DATASOURCE_PASSWORD='password'
4.
Set Spring env vars in Deployment (k8s/deployment.yaml):
env:
- name: SPRING_DATASOURCE_URL
value: jdbc:postgresql://<rds-endpoint>:5432/<db-name>
- name: SPRING_DATASOURCE_USERNAME
valueFrom:
secretKeyRef:
name: db-secret
key: SPRING_DATASOURCE_USERNAME
- name: SPRING_DATASOURCE_PASSWORD
valueFrom:
secretKeyRef:
name: db-secret
key: SPRING_DATASOURCE_PASSWORD
5.
Add dependencies/config (if not already):
◦
spring-boot-starter-data-jpa
◦
org.postgresql:postgresql
◦
optional in application.properties:
▪
spring.jpa.hibernate.ddl-auto=update
▪
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
6.
Apply and restart:
kubectl apply -f k8s/
kubectl rollout restart deployment/spring-pipeline
kubectl logs -f deployment/spring-pipeline
For production, prefer AWS Secrets Manager + External Secrets (instead of plain K8s secret literals) and keep RDS non-public.

Connection steps
Follow the steps below to paste the code of each step in your tool and run the commands. The snippets dynamically reflect the authentication configuration.
Endpoint: postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com
Port: 5432
Database: postgresDB

curl -o global-bundle.pem https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem

export RDSHOST="postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com"
psql "host=$RDSHOST port=5432 dbname=postgres user=postgres sslmode=verify-full sslrootcert=./global-bundle.pem"

Use a direct PostgreSQL client check plus a TCP check.
# 1) DNS + port reachability
nslookup postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com
nc -vz postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com 5432

# 2) Real DB login test (requires psql installed)
PGPASSWORD='password' psql \
-h postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com \
-p 5432 \
-U postgres \
-d postgresDB \
-c "select current_database(), current_user, version();"
If nc fails, it’s network/security group/routing.
If nc succeeds but psql fails, it’s credentials/DB name/SSL/auth policy.
For RDS SSL-required setups, use:
PGPASSWORD='password' psql \
"host=postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com port=5432 dbname=postgresDB user=postgres sslmode=require" \
-c "select now();"

Fork Chat
GPT-5.3-Codex • 0.9 Credits


what is :  curl -s https://checkip.amazonaws.com
73.8.22.123  ?
README.md:384-389
That is your public IPv4 address as seen from Amazon’s checkip service.
So:
•
73.8.22.123 is the internet IP your machine is using right now
•
it can be used in a firewall rule like:
aws ec2 authorize-security-group-ingress \
--group-id sg-0d2e0f73c9e802bd9 \
--protocol tcp \
--port 5432 \
--cidr 73.8.22.123/32 \
--region us-east-2

1.
Modify the RDS instance to be publicly accessible:
aws rds modify-db-instance \
--db-instance-identifier postgresdb \
--publicly-accessible \
--apply-immediately \
--region us-east-2
3.
Add SG rule for your current public IP (tight scope, port 5432 only):
MYIP=$(curl -s https://checkip.amazonaws.com)
aws ec2 authorize-security-group-ingress \
--group-id sg-0d2e0f73c9e802bd9 \
--protocol tcp \
--port 5432 \
--cidr ${MYIP}/32 \
--region us-east-2


//create secret
kubectl create secret generic db-secret \
--from-literal=SPRING_DATASOURCE_USERNAME='postgres' \
--from-literal=SPRING_DATASOURCE_PASSWORD='password'

garyboriskin@Garys-MacBook-Air spring-pipeline % kubectl create secret generic db-secret \
--from-literal=SPRING_DATASOURCE_USERNAME='postgres' \
--from-literal=SPRING_DATASOURCE_PASSWORD='password'
secret/db-secret created

4.
Set Spring env vars in Deployment (k8s/deployment.yaml):
endpoint
postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com
env:
- name: SPRING_DATASOURCE_URL
value: jdbc:postgresql://postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com:5432/<db-name>
- name: SPRING_DATASOURCE_USERNAME
valueFrom:
secretKeyRef:
name: db-secret
key: SPRING_DATASOURCE_USERNAME
- name: SPRING_DATASOURCE_PASSWORD
valueFrom:
secretKeyRef:
name: db-secret
key: SPRING_DATASOURCE_PASSWORD
  what is the command to see rds-endpoint?
  Use the AWS CLI:
  aws rds describe-db-instances \
  --db-instance-identifier postgresdb \
  --region us-east-2 \
  --query "DBInstances[0].Endpoint.Address" \
  --output text

--connect to db
psql --host=postgresdb.cp484oko83ef.us-east-2.rds.amazonaws.com --port=5432 --dbname=postgres --username=postgres
--ctreate table
CREATE TABLE customers (
customer_id SERIAL PRIMARY KEY,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
email VARCHAR(255) UNIQUE NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO customers (first_name, last_name, email) VALUES ('John', 'Doe', 'joedoe@gmail.com');
INSERT INTO customers (first_name, last_name, email) VALUES ('Mary', 'Smith', 'marysmith@gmail.com');

to find the db name

aws rds describe-db-instances \
--db-instance-identifier postgresdb \
--region us-east-2 \
--query "DBInstances[0].DBName" \
--output text
If it returns blank, then this RDS instance was created without a default DB name, or it uses the default postgres database.
For your case, this is the one that worked from the pod:
-d postgres
So the DB name is usually:
•
postgres

## Test JWT with curl

Use these commands to verify JWT authentication flow end-to-end.

Set your base URL:

```sh
# Local Maven run (default Spring Boot port)
export BASE_URL="http://localhost:8080"

# If you run the Docker command from this README, use this instead:
# export BASE_URL="http://localhost:8081"
```

1. Verify protected endpoint is blocked without a token:

```sh
curl -i "$BASE_URL/api/requests"
```

Expected: `401 Unauthorized`.

2. Request a JWT token:

```sh
curl -s -X POST "$BASE_URL/api/auth/token" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"changeit"}'
```

Expected response shape:

```json
{"token":"<jwt_here>"}
```
{"token":"eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc5NDU1MDUyMCwiZXhwIjoxNzk0NTU0MTIwfQ.4xS7Rg_jps4KhyOA7L2z1uOxpxGNdey_d4MwoJnoK4HhxLwwVFcrq5Ipdpom0nZ6"}%

3. Save token and call protected API:

Short `jq` one-liner (if `jq` is installed):

```sh
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/token" -H "Content-Type: application/json" -d '{"username":"admin","password":"changeit"}' | jq -r '.token')
```

```sh
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/token" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"changeit"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -i "$BASE_URL/api/requests" \
  -H "Authorization: Bearer $TOKEN"
```

Expected: `200 OK`.

4. Optional negative checks:

```sh
# Invalid credentials -> 401
curl -i -X POST "$BASE_URL/api/auth/token" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong"}'

# Invalid token -> 401
curl -i "$BASE_URL/api/requests" \
  -H "Authorization: Bearer not-a-real-token"
```

#Craete a new secret for JWT

TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/token" -H "Content-Type: application/json" -d '{"username":"admin","password":"changeit"}' | jq -r '.token')

kubectl create secret generic auth-secret \
--from-literal=SECURITY_JWT_SECRET='eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc5NDU3NzY3OSwiZXhwIjoxNzk0NTgxMjc5fQ.K_z26_6ftJC5bXI0G8-iPzzXWBxOiyrs_KA_Z_vqYZD_DYnP_WY3k_IsMgsw1b1Y' \
--from-literal=SECURITY_AUTH_USERNAME='admin' \
--from-literal=SECURITY_AUTH_PASSWORD='changeit'

#apply  k8s/deployment.yaml to update the pod with the new secret.  Restart the pod to pick up the new secret.

kubectl apply -f k8s/deployment.yaml
kubectl rollout status deployment/spring-pipeline

