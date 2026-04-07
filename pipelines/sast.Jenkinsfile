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

                        # Calcul SonarQube
                        if [ -f reports/sast/sonar-report.json ]; then
                            SONAR_TOTAL=$(jq '.total // 0' reports/sast/sonar-report.json)
                            VULN=$(jq '[.issues[] | select(.type=="VULNERABILITY")] | length' reports/sast/sonar-report.json)
                            BUGS=$(jq '[.issues[] | select(.type=="BUG")] | length' reports/sast/sonar-report.json)
                            SMELLS=$(jq '[.issues[] | select(.type=="CODE_SMELL")] | length' reports/sast/sonar-report.json)
                        else
                            SONAR_TOTAL=0
                            VULN=0
                            BUGS=0
                            SMELLS=0
                        fi

                        # Calcul Gitleaks
                        if [ -f reports/sast/gitleaks-report.json ]; then
                            GITLEAKS_TOTAL=$(jq 'length' reports/sast/gitleaks-report.json)
                        else
                            GITLEAKS_TOTAL=0
                        fi

                        # Création du summary.txt
                        echo "==============================" > reports/sast/summary.txt
                        echo "     SAST SECURITY REPORT     " >> reports/sast/summary.txt
                        echo "==============================" >> reports/sast/summary.txt
                        echo "Project       : devsecops-test" >> reports/sast/summary.txt
                        echo "Generated on  : $(date)" >> reports/sast/summary.txt
                        echo "" >> reports/sast/summary.txt

                        echo "---------- SONARQUBE ----------" >> reports/sast/summary.txt
                        echo "Total Issues     : $SONAR_TOTAL" >> reports/sast/summary.txt
                        echo "Vulnerabilities  : $VULN" >> reports/sast/summary.txt
                        echo "Bugs             : $BUGS" >> reports/sast/summary.txt
                        echo "Code Smells      : $SMELLS" >> reports/sast/summary.txt
                        echo "" >> reports/sast/summary.txt

                        echo "---------- GITLEAKS ----------" >> reports/sast/summary.txt
                        echo "Total Findings   : $GITLEAKS_TOTAL" >> reports/sast/summary.txt
                        echo "" >> reports/sast/summary.txt

                        echo "---------- FINAL STATUS ----------" >> reports/sast/summary.txt
                        if [ "$GITLEAKS_TOTAL" -gt 0 ] || [ "$VULN" -gt 0 ]; then
                            echo "[WARNING] Security issues detected" >> reports/sast/summary.txt
                        else
                            echo "[OK] No critical issues" >> reports/sast/summary.txt
                        fi
                        echo "" >> reports/sast/summary.txt
                        echo "End of Report" >> reports/sast/summary.txt

                        # Création du summary.html
                        cat <<EOF > reports/sast/summary.html
<html>
<head>
    <title>SAST Report</title>
</head>
<body>
    <h1>SAST SECURITY REPORT</h1>
    <p><strong>Project:</strong> devsecops-test</p>
    <p><strong>Generated on:</strong> $(date)</p>

    <h2>SonarQube</h2>
    <ul>
        <li>Total Issues: $SONAR_TOTAL</li>
        <li>Vulnerabilities: $VULN</li>
        <li>Bugs: $BUGS</li>
        <li>Code Smells: $SMELLS</li>
    </ul>

    <h2>Gitleaks</h2>
    <ul>
        <li>Total Findings: $GITLEAKS_TOTAL</li>
    </ul>

    <h2>Final Status</h2>
EOF

                        if [ "$GITLEAKS_TOTAL" -gt 0 ] || [ "$VULN" -gt 0 ]; then
                            echo "    <p>[WARNING] Security issues detected</p>" >> reports/sast/summary.html
                        else
                            echo "    <p>[OK] No critical issues</p>" >> reports/sast/summary.html
                        fi

                        cat <<EOF >> reports/sast/summary.html
</body>
</html>
EOF
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