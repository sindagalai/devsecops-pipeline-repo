def printBanner(String message) {
    echo "========================================"
    echo "${message}"
    echo "========================================"
}

def ensureReportDirectory(String path) {
    sh "mkdir -p ${path}"
}

def printCurrentEnvironment() {
    sh 'echo "Running on agent: $(hostname)"'
    sh 'whoami'
    sh 'java -version'
    sh 'mvn -version'
    sh 'docker --version'
    sh 'git --version'
    sh 'jq --version'
    sh 'curl --version | head -n 1'
}

def archiveReport(String pattern) {
    archiveArtifacts artifacts: pattern, fingerprint: true
}

return this