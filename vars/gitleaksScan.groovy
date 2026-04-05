def call() {
    sh '''
        mkdir -p reports/sast

        echo "Running Gitleaks scan..."

        gitleaks dir . \
          --report-format=json \
          --report-path=reports/sast/gitleaks-report.json || true
    '''
}