pipeline {

    agent {
        docker {
            image 'jenkins/inbound-agent:3261.v9c670a_4748a_9-1'  // Utilise l'image Docker de ton agent
            args '-v /var/run/docker.sock:/var/run/docker.sock'  // Permet à Docker de s'exécuter à l'intérieur du conteneur
        }
    }

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

                        # Création du summary.html amélioré
                        cat <<EOF > reports/sast/summary.html
<html>
<head>
    <title>SAST Report</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 30px;
            color: #2c3e50;
        }
        .container {
            max-width: 900px;
            margin: auto;
        }
        h1 {
            text-align: center;
            color: #1f4e79;
            margin-bottom: 30px;
        }
        .card {
            background: white;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        h2 {
            margin-top: 0;
            color: #1f4e79;
            border-bottom: 2px solid #e5e7eb;
            padding-bottom: 8px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            text-align: left;
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f8fafc;
        }
        .status-ok {
            color: green;
            font-weight: bold;
        }
        .status-warning {
            color: orange;
            font-weight: bold;
        }
        .meta {
            font-size: 15px;
            line-height: 1.8;
        }
    </style>
</head>
<body>
<div class="container">

    <h1>SAST SECURITY REPORT</h1>

    <div class="card">
        <div class="meta">
            <strong>Project:</strong> devsecops-test<br>
            <strong>Generated on:</strong> $(date)
        </div>
    </div>

    <div class="card">
        <h2>SonarQube Results</h2>
        <table>
            <tr><th>Metric</th><th>Value</th></tr>
            <tr><td>Total Issues</td><td>$SONAR_TOTAL</td></tr>
            <tr><td>Vulnerabilities</td><td>$VULN</td></tr>
            <tr><td>Bugs</td><td>$BUGS</td></tr>
            <tr><td>Code Smells</td><td>$SMELLS</td></tr>
        </table>
    </div>

    <div class="card">
        <h2>Gitleaks Results</h2>
        <table>
            <tr><th>Metric</th><th>Value</th></tr>
            <tr><td>Total Findings</td><td>$GITLEAKS_TOTAL</td></tr>
        </table>
    </div>

    <div class="card">
        <h2>Final Status</h2>
EOF

                        if [ "$GITLEAKS_TOTAL" -gt 0 ] || [ "$VULN" -gt 0 ]; then
                            echo '        <p class="status-warning">[WARNING] Security issues detected</p>' >> reports/sast/summary.html
                        else
                            echo '        <p class="status-ok">[OK] No critical issues</p>' >> reports/sast/summary.html
                        fi

                        cat <<EOF >> reports/sast/summary.html
    </div>

</div>
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