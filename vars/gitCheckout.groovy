def call(String repoUrl = 'https://github.com/sindagalai/DevSecOpsTest.git', String branchName = 'main') {
    // Cloner le dépôt Git cible avec la branche spécifiée
    git branch: branchName,
        credentialsId: 'github-creds',
        url: repoUrl
}

return this