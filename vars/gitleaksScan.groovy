def call() {
    sh '''
        mkdir -p reports/sast

        echo "Running Gitleaks scan..."

        gitleaks dir . \
          --config=.gitleaks.toml \
          --report-format=json \
          --report-path=reports/sast/gitleaks-report.json || true
    '''
}

return this