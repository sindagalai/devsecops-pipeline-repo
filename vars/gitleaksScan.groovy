def call() {
    sh '''
        # Créer le dossier de rapport
        mkdir -p reports/sast

        echo "Running Gitleaks scan..."

        # Vérification du contexte courant
        pwd
        ls -la

        # Vérifier si le fichier de config existe
        if [ -f .gitleaks.toml ]; then
            echo ".gitleaks.toml found"
            CONFIG_ARG="--config=/repo/.gitleaks.toml"
        else
            echo ".gitleaks.toml not found, running with default config"
            CONFIG_ARG=""
        fi

        # Lancer Gitleaks dans un conteneur Docker
        docker run --rm \
          -v "$PWD":/repo \
          -w /repo \
          zricethezav/gitleaks:latest detect \
          --source=/repo \
          $CONFIG_ARG \
          --report-format=json \
          --report-path=/repo/reports/sast/gitleaks-report.json || true

        echo "Gitleaks report content:"
        if [ -f reports/sast/gitleaks-report.json ]; then
            cat reports/sast/gitleaks-report.json
        else
            echo "No gitleaks report generated"
        fi
    '''
}

return this