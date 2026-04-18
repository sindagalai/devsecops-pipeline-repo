def call() {
    sh '''
        set -e

        mkdir -p reports/sast

        echo "========================================"
        echo "Running Gitleaks scan"
        echo "========================================"

        pwd
        ls -la

        if [ -f .gitleaks.toml ]; then
            echo ".gitleaks.toml found"
            CONFIG_ARG="--config=/repo/.gitleaks.toml"
        else
            echo ".gitleaks.toml not found, using default Gitleaks config"
            CONFIG_ARG=""
        fi

        docker run --rm \
          -v "$PWD":/repo \
          -w /repo \
          zricethezav/gitleaks:latest detect \
          --source=/repo \
          $CONFIG_ARG \
          --report-format=json \
          --report-path=/repo/reports/sast/gitleaks-report.json \
          --exit-code 0 \
          --verbose

        echo "========================================"
        echo "Gitleaks report preview"
        echo "========================================"

        if [ -f reports/sast/gitleaks-report.json ]; then
            cat reports/sast/gitleaks-report.json
        else
            echo "[]"> reports/sast/gitleaks-report.json
            echo "Empty Gitleaks report created"
        fi
    '''
}

return this