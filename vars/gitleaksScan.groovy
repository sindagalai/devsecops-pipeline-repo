def call() {
    sh '''
        set -e

        mkdir -p reports/sast

        echo "========================================"
        echo "Running Gitleaks scan"
        echo "========================================"

        pwd
        ls -la
        ls -la .git || true
        ls -la reports || true
        ls -la reports/sast || true

        docker run --rm \
          -e GIT_DISCOVERY_ACROSS_FILESYSTEM=1 \
          -v "$PWD":/repo \
          -v "$PWD/reports/sast":/report \
          -w /repo \
          zricethezav/gitleaks:latest detect \
          --source=/repo \
          --report-format=json \
          --report-path=/report/gitleaks-report.json \
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