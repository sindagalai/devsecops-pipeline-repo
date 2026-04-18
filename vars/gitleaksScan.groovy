def call() {
    sh '''
        set -e

        echo "=== DEBUG WORKSPACE ==="
        pwd
        ls -la
        find . -type f | head -50 || true

        mkdir -p reports/sast
        rm -f reports/sast/gitleaks-report.json

        echo "=== GITLEAKS VERSION ==="
        gitleaks version

        CONFIG_FILE=""

        if [ -f ".gitleaks.toml" ]; then
            echo "Project-specific .gitleaks.toml found"
            CONFIG_FILE=".gitleaks.toml"
        else
            echo "Using default pipeline Gitleaks config"
            mkdir -p /home/jenkins/agent/gitleaks-rules
            cp ${WORKSPACE}/config/.gitleaks-default.toml /home/jenkins/agent/gitleaks-rules/.gitleaks-default.toml
            CONFIG_FILE="/home/jenkins/agent/gitleaks-rules/.gitleaks-default.toml"
        fi

        echo "=== USING CONFIG: ${CONFIG_FILE} ==="

        gitleaks dir . \
          --config "${CONFIG_FILE}" \
          --report-format json \
          --report-path reports/sast/gitleaks-report.json \
          --exit-code 0 \
          --verbose

        if [ ! -f reports/sast/gitleaks-report.json ]; then
            echo "[]" > reports/sast/gitleaks-report.json
        fi

        echo "=== REPORT CONTENT ==="
        cat reports/sast/gitleaks-report.json
    '''
}

return this