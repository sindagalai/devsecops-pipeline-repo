def call() {
    sh '''
        mkdir -p reports/sast

        echo "Running Gitleaks scan..."

        pwd
        ls -la
        ls -la .gitleaks.toml

        gitleaks dir . \
          --config=.gitleaks.toml \
          --report-format=json \
          --report-path=reports/sast/gitleaks-report.json || true

        echo "Gitleaks report content:"
        cat reports/sast/gitleaks-report.json
    '''
}

return this