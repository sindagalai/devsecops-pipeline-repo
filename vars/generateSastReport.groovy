def call() {
    sh '''
        # Créer le répertoire pour les rapports
        mkdir -p reports/sast

        # Récupérer le rapport SonarQube au format JSON
        curl -s -u ${SONAR_TOKEN}: "http://localhost:9000/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

        # Générer un fichier résumé
        echo "SAST Report - SonarQube" > reports/sast/summary.txt
        echo "Project: devsecops-test" >> reports/sast/summary.txt
        echo "Generated on: $(date)" >> reports/sast/summary.txt

        # Extraire les données du JSON et les transformer en format CSV
        jq -r '.issues[] | [.severity, .component, .line, .message] | @csv' reports/sast/sonar-report.json > reports/sast/sonar-report.csv

        # Générer un rapport HTML avec les résultats
        echo "<html><head><style>" > reports/sast/sonar-report.html
        echo "table {border-collapse: collapse; width: 100%;}" >> reports/sast/sonar-report.html
        echo "th, td {padding: 8px; text-align: left; border: 1px solid #ddd;}" >> reports/sast/sonar-report.html
        echo "th {background-color: #f2f2f2;}" >> reports/sast/sonar-report.html
        echo ".blocker {background-color: #ff4c4c; color: white;}" >> reports/sast/sonar-report.html
        echo ".major {background-color: #ff9b3d; color: white;}" >> reports/sast/sonar-report.html
        echo ".minor {background-color: #85e085; color: black;}" >> reports/sast/sonar-report.html
        echo "</style></head><body><h1>SAST Report - SonarQube</h1>" >> reports/sast/sonar-report.html
        echo "<h2>Project: devsecops-test</h2>" >> reports/sast/sonar-report.html
        echo "<h3>Generated on: $(date)</h3>" >> reports/sast/sonar-report.html
        echo "<table><tr><th>Severity</th><th>File</th><th>Line</th><th>Message</th></tr>" >> reports/sast/sonar-report.html

        # Ajouter des lignes dans le tableau HTML avec des couleurs en fonction de la gravité
        jq -r '.issues[] | "<tr class=\"\(.severity | ascii_downcase)\"><td>\(.severity)</td><td>\(.component)</td><td>\(.line)</td><td>\(.message)</td></tr>"' reports/sast/sonar-report.json >> reports/sast/sonar-report.html

        echo "</table></body></html>" >> reports/sast/sonar-report.html
    '''
}