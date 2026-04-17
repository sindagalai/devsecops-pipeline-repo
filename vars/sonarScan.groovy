def call() {
    sh '''
        echo "Checking SonarQube..."
        curl -f ${SONAR_HOST_URL}

        echo "Running lightweight SonarQube scan..."

        mvn clean compile sonar:sonar -DskipTests \
          -Dsonar.projectKey=devsecops-test \
          -Dsonar.projectName=DevSecOpsTest \
          -Dsonar.host.url=${SONAR_HOST_URL} \
          -Dsonar.token=${SONAR_TOKEN}
    '''
}

return this