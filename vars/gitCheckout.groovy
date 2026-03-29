def call() {
    git branch: 'main',
        credentialsId: 'github-creds',
        url: 'https://github.com/sindagalai/DevSecOpsTest.git'
}