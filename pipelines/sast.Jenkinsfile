pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Checkout Code') {
            steps {
                dir('source-code') {
                    git branch: 'main',
                        credentialsId: 'github-creds',
                        url: 'https://github.com/sindagalai/DevSecOpsTest.git'
                }
            }
        }

        stage('Run SonarQube Scan') {
            steps {
                dir('source-code') {
                    sh """
                        mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=devsecops-test \
                        -Dsonar.projectName=DevSecOpsTest \
                        -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Run Gitleaks Scan') {
            steps {
                dir('source-code') {
                    script {
                        def gitleaksScan = load "${WORKSPACE}/vars/gitleaksScan.groovy"
                        gitleaksScan()
                    }
                }
            }
        }

        stage('Generate SAST Report') {
            steps {
                dir('source-code') {
                    sh '''
                        mkdir -p reports/sast

                        curl -s -u ${SONAR_TOKEN}: "http://localhost:9000/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

                        echo "SAST Report - SonarQube + Gitleaks" > reports/sast/summary.txt
                        echo "Project: devsecops-test" >> reports/sast/summary.txt
                        echo "Generated on: $(date)" >> reports/sast/summary.txt

                        SONAR_TOTAL=$(jq '.total' reports/sast/sonar-report.json 2>/dev/null || echo 0)
                        GITLEAKS_TOTAL=$(jq 'length' reports/sast/gitleaks-report.json 2>/dev/null || echo 0)

                        echo "" >> reports/sast/summary.txt
                        echo "SonarQube Issues: $SONAR_TOTAL" >> reports/sast/summary.txt
                        echo "Gitleaks Findings: $GITLEAKS_TOTAL" >> reports/sast/summary.txt
                    '''
                }
            }
        }

        stage('Archive SAST Report') {
            steps {
                archiveArtifacts artifacts: 'source-code/reports/sast/**', fingerprint: true
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