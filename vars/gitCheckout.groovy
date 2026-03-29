def call(String repoUrl, String branch) {
    echo "Clonage du repo ${repoUrl} sur la branche ${branch}"
    git branch: branch, url: repoUrl
}

return this