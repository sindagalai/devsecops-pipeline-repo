def call() {
    sh """
        # Lancer l'analyse SonarQube avec Maven
        mvn clean verify sonar:sonar \
        -Dsonar.projectKey=devsecops-test \
        -Dsonar.projectName=DevSecOpsTest \
        -Dsonar.host.url=${SONAR_HOST_URL} \
        -Dsonar.token=${SONAR_TOKEN}
    """
}