def call() {
    sh '''
        set -e

        echo "========================================"
        echo "Checking SonarQube availability"
        echo "========================================"
        curl -f ${SONAR_HOST_URL}

        echo "========================================"
        echo "Running full SonarQube SAST scan"
        echo "========================================"

        mvn -B -ntp clean verify sonar:sonar \
          -DskipTests \
          -Dsonar.projectKey=devsecops-test \
          -Dsonar.projectName=DevSecOpsTest \
          -Dsonar.host.url=${SONAR_HOST_URL} \
          -Dsonar.token=${SONAR_TOKEN}
    '''
}

return this