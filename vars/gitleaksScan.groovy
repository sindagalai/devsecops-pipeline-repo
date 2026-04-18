def call() {
    sh '''
        set -e

        mkdir -p reports/sast

        echo "========================================"
        echo "Running Gitleaks scan"
        echo "========================================"

        pwd
        ls -la

        # Scan Gitleaks sans dépendre du fichier .gitleaks.toml
        docker run --rm \
          -v "$PWD":/repo \
          -w /repo \
          zricethezav/gitleaks:latest detect \
          --source=/repo \
          --report-format=json \
          --report-path=/repo/reports/sast/gitleaks-report.json \
          --exit-code 0 \
          --verbose

        # Garantir l'existence du rapport
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