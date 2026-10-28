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
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn -B clean verify'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -DskipTests package'
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
                if (env.CHANGE_ID) {
                    echo "PR #${env.CHANGE_ID} passed for ${env.CHANGE_BRANCH} -> ${env.CHANGE_TARGET}"
                }
            }
        }
    }
}

