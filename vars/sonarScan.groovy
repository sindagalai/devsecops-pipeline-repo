def call() {
    echo "Lancement du scan SonarQube..."

    withSonarQubeEnv('sonarqube') {
        sh '''
            mvn clean verify sonar:sonar \
              -Dsonar.projectKey=devsecops-test \
              -Dsonar.projectName=DevSecOpsTest
        '''
    }
}