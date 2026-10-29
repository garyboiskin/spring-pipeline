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
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
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
                withEnv([
                    'PATH+EXTRA=/usr/local/bin:/usr/bin',
                    "KUBECONFIG=${env.HOME}/.kube/config"
                ]) {
                    sh '''
                        docker build -t spring-pipeline:latest .
                        kubectl apply -f k8s/
                        kubectl rollout restart deployment/spring-pipeline
                        kubectl rollout status deployment/spring-pipeline --timeout=120s
                    '''
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

