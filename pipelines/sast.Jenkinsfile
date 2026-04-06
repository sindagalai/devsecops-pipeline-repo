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

                        # Télécharger le rapport SonarQube
                        curl -s -u ${SONAR_TOKEN}: "http://localhost:9000/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

                        # Création du fichier summary
                        echo "==============================" > reports/sast/summary.txt
                        echo "     SAST SECURITY REPORT     " >> reports/sast/summary.txt
                        echo "==============================" >> reports/sast/summary.txt
                        echo "Project       : devsecops-test" >> reports/sast/summary.txt
                        echo "Generated on  : $(date)" >> reports/sast/summary.txt
                        echo "" >> reports/sast/summary.txt

                        # Calcul SonarQube
                        if [ -f reports/sast/sonar-report.json ]; then
                            SONAR_TOTAL=$(jq '.total // 0' reports/sast/sonar-report.json)
                        else
                            SONAR_TOTAL=0
                        fi

                        # Calcul Gitleaks
                        if [ -f reports/sast/gitleaks-report.json ]; then
                            GITLEAKS_TOTAL=$(jq 'length' reports/sast/gitleaks-report.json)
                        else
                            GITLEAKS_TOTAL=0
                        fi

                        # Résultats
                        echo "---------- RESULTS -----------" >> reports/sast/summary.txt
                        echo "SonarQube Issues : $SONAR_TOTAL" >> reports/sast/summary.txt
                        echo "Gitleaks Findings: $GITLEAKS_TOTAL" >> reports/sast/summary.txt

                        echo "--------------------------------" >> reports/sast/summary.txt

                        # Indicateur sécurité
                        if [ "$GITLEAKS_TOTAL" -gt 0 ]; then
                            echo "❌ Sensitive data detected!" >> reports/sast/summary.txt
                        else
                            echo "✅ No secrets detected" >> reports/sast/summary.txt
                        fi

                        echo "--------------------------------" >> reports/sast/summary.txt
                        echo "End of Report" >> reports/sast/summary.txt
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