def call() {
    sh """
        mvn clean verify sonar:sonar \
        -Dsonar.projectKey=devsecops-test \
        -Dsonar.projectName=DevSecOpsTest \
        -Dsonar.token=${SONAR_TOKEN}
    """
}