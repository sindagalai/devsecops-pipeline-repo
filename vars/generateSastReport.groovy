def call() {
    sh '''
        # Créer le dossier des rapports SAST
        mkdir -p reports/sast

        # Télécharger les issues SonarQube via l’API REST
        curl -s -u ${SONAR_TOKEN}: "${SONAR_HOST_URL}/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

        # Créer le fichier résumé initial
        echo "SAST Report - SonarQube" > reports/sast/summary.txt
        echo "Project: devsecops-test" >> reports/sast/summary.txt
        echo "Generated on: $(date)" >> reports/sast/summary.txt
    '''
}

return this