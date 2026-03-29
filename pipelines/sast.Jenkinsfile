pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    gitCheckout()
                }
            }
        }

        stage('Run SonarQube Scan') {
            steps {
                script {
                    sonarScan()
                }
            }
        }

        stage('Generate SAST Report') {
            steps {
                script {
                    generateSastReport()
                }
            }
        }

        stage('Archive SAST Report') {
            steps {
                archiveArtifacts artifacts: 'reports/sast/**', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'SAST pipeline completed successfully.'
        }
        failure {
            echo 'SAST pipeline failed.'
        }
    }
}