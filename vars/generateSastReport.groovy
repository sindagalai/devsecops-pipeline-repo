def call() {
    sh '''
        mkdir -p reports/sast

        curl -s -u ${SONAR_TOKEN}: "${SONAR_HOST_URL}/api/issues/search?componentKeys=devsecops-test&ps=500" -o reports/sast/sonar-report.json

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

        if [ -f reports/sast/gitleaks-report.json ]; then
            GITLEAKS_TOTAL=$(jq 'length' reports/sast/gitleaks-report.json)
        else
            GITLEAKS_TOTAL=0
        fi

        {
          echo "=============================="
          echo "     SAST SECURITY REPORT     "
          echo "=============================="
          echo "Project       : devsecops-test"
          echo "Generated on  : $(date)"
          echo
          echo "---------- SONARQUBE ----------"
          echo "Total Issues     : $SONAR_TOTAL"
          echo "Vulnerabilities  : $VULN"
          echo "Bugs             : $BUGS"
          echo "Code Smells      : $SMELLS"
          echo
          echo "---------- GITLEAKS ----------"
          echo "Total Findings   : $GITLEAKS_TOTAL"
          echo
          echo "---------- FINAL STATUS ----------"
          if [ "$GITLEAKS_TOTAL" -gt 0 ] || [ "$VULN" -gt 0 ]; then
              echo "[WARNING] Security issues detected"
          else
              echo "[OK] No critical issues"
          fi
        } > reports/sast/summary.txt
    '''
}

return this