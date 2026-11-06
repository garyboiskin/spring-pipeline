pipeline {
    agent any
        tools {
            // This must match the name configured in 'Global Tool Configuration'
            maven 'Maven 3.10'
        }

    options {
        disableConcurrentBuilds()
        timestamps()
    }

    // Polling is useful for local Jenkins when GitHub webhooks are not reachable.
    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        MAVEN_OPTS     = '-Dmaven.repo.local=.m2/repository'
        AWS_REGION     = 'us-east-2'
        AWS_ACCOUNT_ID = '839670623406'
        CLUSTER_NAME   = 'spring-boot-cluster'
        ECR_REPO       = 'spring-pipeline'
        IMAGE          = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                 echo 'Checkout'
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                 echo 'Build and Test...'
                sh 'mvn -B clean verify'
            }
        }

        stage('Package') {
            steps {
                 echo 'Package...'
                sh 'mvn -B -DskipTests package'
            }
        }

        stage('Deploy') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                echo 'Deploy...'
                withEnv(['PATH+EXTRA=/usr/local/bin:/usr/bin:/opt/homebrew/bin']) {
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
                            kubectl rollout status deployment/spring-pipeline --timeout=380s
                        '''
                    }
                }
            }
        }
    }



    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
        }

        success {
            script {
                 echo 'Success...'
                if (env.CHANGE_ID) {
                    echo "PR #${env.CHANGE_ID} passed for ${env.CHANGE_BRANCH} -> ${env.CHANGE_TARGET}"
                }
            }
        }
    }
}

