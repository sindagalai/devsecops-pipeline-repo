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
            echo ".gitleaks.toml not found, using default config"
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