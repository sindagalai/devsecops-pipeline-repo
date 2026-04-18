def call() {
    sh '''
        set -e

        mkdir -p reports/sast
        rm -f reports/sast/gitleaks-report.json

        echo "========================================"
        echo "Running Gitleaks directory scan"
        echo "========================================"

        pwd
        ls -la
        ls -la reports || true
        ls -la reports/sast || true

        docker run --rm \
          -v "$PWD":/repo \
          -v "$PWD/reports/sast":/report \
          -w /repo \
          zricethezav/gitleaks:latest \
          dir /repo \
          --report-format json \
          --report-path /report/gitleaks-report.json \
          --exit-code 0 \
          --verbose

        if [ ! -f reports/sast/gitleaks-report.json ]; then
            echo "[]"> reports/sast/gitleaks-report.json
        fi

        echo "========================================"
        echo "Gitleaks report generated"
        echo "========================================"
        cat reports/sast/gitleaks-report.json
    '''
}

return this