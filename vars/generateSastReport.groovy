def call() {
    sh '''
        set -e

        mkdir -p reports/sast

        echo "========================================"
        echo "Downloading SonarQube data"
        echo "========================================"

        curl -s -u ${SONAR_TOKEN}: \
          "${SONAR_HOST_URL}/api/issues/search?componentKeys=devsecops-test&ps=500" \
          -o reports/sast/sonar-report.json

        curl -s -u ${SONAR_TOKEN}: \
          "${SONAR_HOST_URL}/api/measures/component?component=devsecops-test&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,coverage,duplicated_lines_density,reliability_rating,security_rating,sqale_rating,alert_status" \
          -o reports/sast/sonar-measures.json

        if [ ! -f reports/sast/gitleaks-report.json ]; then
            echo "[]"> reports/sast/gitleaks-report.json
        fi

        SONAR_TOTAL=$(jq '.total // 0' reports/sast/sonar-report.json)

        BUGS=$(jq -r '.component.measures[] | select(.metric=="bugs") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        VULN=$(jq -r '.component.measures[] | select(.metric=="vulnerabilities") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        SMELLS=$(jq -r '.component.measures[] | select(.metric=="code_smells") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        HOTSPOTS=$(jq -r '.component.measures[] | select(.metric=="security_hotspots") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        COVERAGE=$(jq -r '.component.measures[] | select(.metric=="coverage") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        DUPLICATION=$(jq -r '.component.measures[] | select(.metric=="duplicated_lines_density") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        RELIABILITY=$(jq -r '.component.measures[] | select(.metric=="reliability_rating") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        SECURITY=$(jq -r '.component.measures[] | select(.metric=="security_rating") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        MAINTAINABILITY=$(jq -r '.component.measures[] | select(.metric=="sqale_rating") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)
        QUALITY_GATE=$(jq -r '.component.measures[] | select(.metric=="alert_status") | .value' reports/sast/sonar-measures.json 2>/dev/null || true)

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

        GITLEAKS_TOTAL=$(jq 'length' reports/sast/gitleaks-report.json 2>/dev/null || echo 0)

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

        cat <<EOF > reports/sast/summary.html
<html>
<head>
    <title>SAST Report</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f8; padding: 30px; color: #2c3e50; }
        .container { max-width: 900px; margin: auto; }
        .card { background: white; padding: 20px; margin-bottom: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
        h1 { text-align: center; color: #1f4e79; }
        h2 { color: #1f4e79; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { text-align: left; padding: 10px; border-bottom: 1px solid #ddd; }
        th { background: #f8fafc; }
        .ok { color: green; font-weight: bold; }
        .warn { color: orange; font-weight: bold; }
    </style>
</head>
<body>
<div class="container">
    <h1>SAST SECURITY REPORT</h1>

    <div class="card">
        <p><strong>Project:</strong> devsecops-test</p>
        <p><strong>Generated on:</strong> $(date)</p>
    </div>

    <div class="card">
        <h2>SonarQube</h2>
        <table>
            <tr><th>Metric</th><th>Value</th></tr>
            <tr><td>Total Issues</td><td>$SONAR_TOTAL</td></tr>
            <tr><td>Bugs</td><td>$BUGS</td></tr>
            <tr><td>Vulnerabilities</td><td>$VULN</td></tr>
            <tr><td>Code Smells</td><td>$SMELLS</td></tr>
            <tr><td>Security Hotspots</td><td>$HOTSPOTS</td></tr>
            <tr><td>Coverage (%)</td><td>$COVERAGE</td></tr>
            <tr><td>Duplication (%)</td><td>$DUPLICATION</td></tr>
            <tr><td>Reliability Rating</td><td>$RELIABILITY</td></tr>
            <tr><td>Security Rating</td><td>$SECURITY</td></tr>
            <tr><td>Maintainability Rating</td><td>$MAINTAINABILITY</td></tr>
            <tr><td>Quality Gate</td><td>$QUALITY_GATE</td></tr>
        </table>
    </div>

    <div class="card">
        <h2>Gitleaks</h2>
        <table>
            <tr><th>Metric</th><th>Value</th></tr>
            <tr><td>Total Findings</td><td>$GITLEAKS_TOTAL</td></tr>
        </table>
    </div>

    <div class="card">
        <h2>Final Status</h2>
EOF

        if [ "$GITLEAKS_TOTAL" -gt 0 ] || [ "$VULN" -gt 0 ]; then
            echo '<p class="warn">[WARNING] Security issues detected</p>' >> reports/sast/summary.html
        else
            echo '<p class="ok">[OK] No critical issues detected</p>' >> reports/sast/summary.html
        fi

        cat <<EOF >> reports/sast/summary.html
    </div>
</div>
</body>
</html>
EOF
    '''
}

return this