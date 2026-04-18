def call() {
    sh '''
        set -e

        mkdir -p reports/sast

        if [ -f reports/sast/gitleaks-report.json ]; then
            GITLEAKS_TOTAL=$(jq 'length' reports/sast/gitleaks-report.json 2>/dev/null || echo 0)
        else
            echo "[]"> reports/sast/gitleaks-report.json
            GITLEAKS_TOTAL=0
        fi

        curl -s -u ${SONAR_TOKEN}: \
          "${SONAR_HOST_URL}/api/issues/search?componentKeys=devsecops-test&ps=500" \
          -o reports/sast/sonar-report.json || true

        curl -s -u ${SONAR_TOKEN}: \
          "${SONAR_HOST_URL}/api/measures/component?component=devsecops-test&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,coverage,duplicated_lines_density,reliability_rating,security_rating,sqale_rating,alert_status" \
          -o reports/sast/sonar-measures.json || true

        if [ -f reports/sast/sonar-report.json ]; then
            SONAR_TOTAL=$(jq '.total // 0' reports/sast/sonar-report.json 2>/dev/null || echo 0)
        else
            SONAR_TOTAL=0
        fi

        if [ -f reports/sast/sonar-measures.json ]; then
            BUGS=$(jq -r '.component.measures[]? | select(.metric=="bugs") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            VULN=$(jq -r '.component.measures[]? | select(.metric=="vulnerabilities") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            SMELLS=$(jq -r '.component.measures[]? | select(.metric=="code_smells") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            HOTSPOTS=$(jq -r '.component.measures[]? | select(.metric=="security_hotspots") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            COVERAGE=$(jq -r '.component.measures[]? | select(.metric=="coverage") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            DUPLICATION=$(jq -r '.component.measures[]? | select(.metric=="duplicated_lines_density") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            RELIABILITY=$(jq -r '.component.measures[]? | select(.metric=="reliability_rating") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            SECURITY=$(jq -r '.component.measures[]? | select(.metric=="security_rating") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            MAINTAINABILITY=$(jq -r '.component.measures[]? | select(.metric=="sqale_rating") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
            QUALITY_GATE=$(jq -r '.component.measures[]? | select(.metric=="alert_status") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        fi

        [ -z "$BUGS" ] && BUGS=0
        [ -z "$VULN" ] && VULN=0
        [ -z "$SMELLS" ] && SMELLS=0
        [ -z "$HOTSPOTS" ] && HOTSPOTS=0
        [ -z "$COVERAGE" ] && COVERAGE=0
        [ -z "$DUPLICATION" ] && DUPLICATION=0
        [ -z "$RELIABILITY" ] && RELIABILITY="N/A"
        [ -z "$SECURITY" ] && SECURITY="N/A"
        [ -z "$MAINTAINABILITY" ] && MAINTAINABILITY="N/A"
        [ -z "$QUALITY_GATE" ] && QUALITY_GATE="UNKNOWN"

        {
          echo "=============================="
          echo "       SAST SECURITY REPORT"
          echo "=============================="
          echo "Project               : devsecops-test"
          echo "Generated on          : $(date)"
          echo
          echo "---------- SONARQUBE ----------"
          echo "Total Issues          : $SONAR_TOTAL"
          echo "Bugs                  : $BUGS"
          echo "Vulnerabilities       : $VULN"
          echo "Code Smells           : $SMELLS"
          echo "Security Hotspots     : $HOTSPOTS"
          echo "Coverage (%)          : $COVERAGE"
          echo "Duplication (%)       : $DUPLICATION"
          echo "Reliability Rating    : $RELIABILITY"
          echo "Security Rating       : $SECURITY"
          echo "Maintainability Rating: $MAINTAINABILITY"
          echo "Quality Gate          : $QUALITY_GATE"
          echo
          echo "---------- GITLEAKS ----------"
          echo "Total Findings        : $GITLEAKS_TOTAL"
          echo
          echo "---------- FINAL STATUS ----------"
          if [ "$GITLEAKS_TOTAL" -gt 0 ] || [ "$VULN" -gt 0 ]; then
              echo "[WARNING] Security issues detected"
          else
              echo "[OK] No critical issues detected"
          fi
        } > reports/sast/summary.txt
    '''
}

return this