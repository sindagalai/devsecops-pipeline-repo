def call() {
    sh '''
        set -e

        echo "=== DEBUG WORKSPACE ==="
        pwd
        ls -la

        echo "=== VERIFY GIT ==="
        git status || true

        mkdir -p reports/sast
        rm -f reports/sast/gitleaks-report.json

        echo "========================================"
        echo "Running Gitleaks REAL scan"
        echo "========================================"

        docker run --rm \
          -v "$(pwd)":/repo \
          -w /repo \
          zricethezav/gitleaks:latest \
          dir . \
          --report-format json \
          --report-path reports/sast/gitleaks-report.json \
          --exit-code 0 \
          --verbose

        echo "=== CHECK REPORT ==="
        ls -la reports/sast

        if [ ! -f reports/sast/gitleaks-report.json ]; then
            echo "Report missing → creating empty file"
            echo "[]" > reports/sast/gitleaks-report.json
        fi

        echo "=== REPORT CONTENT ==="
        cat reports/sast/gitleaks-report.json
    '''
}

return this