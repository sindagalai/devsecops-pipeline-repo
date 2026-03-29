pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-creds',
                    url: 'https://github.com/sindagalai/DevSecOpsTest.git'
            }
        }

        stage('Run SonarQube Scan') {
            steps {
                sh """
                    mvn clean verify sonar:sonar \
                    -Dsonar.projectKey=devsecops-test \
                    -Dsonar.projectName=DevSecOpsTest \
                    -Dsonar.token=${SONAR_TOKEN}
                """
            }
        }

        stage('Generate SAST Report') {
            steps {
                sh '''
                    mkdir -p reports/sast

                    curl -s -u ${SONAR_TOKEN}: "http://localhost:9000/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

                    echo "SAST Report - SonarQube" > reports/sast/summary.txt
                    echo "Project: devsecops-test" >> reports/sast/summary.txt
                    echo "Generated on: $(date)" >> reports/sast/summary.txt
                '''
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