def call() {
    sh '''
        mkdir -p reports/sast

        curl -s -u ${SONAR_TOKEN}: "http://localhost:9000/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

        echo "SAST Report - SonarQube" > reports/sast/summary.txt
        echo "Project: devsecops-test" >> reports/sast/summary.txt
        echo "Generated on: $(date)" >> reports/sast/summary.txt
    '''
}