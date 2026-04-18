def call() {
    sh '''
        set +e

        echo "========================================"
        echo "Checking SonarQube availability"
        echo "========================================"
        curl -f ${SONAR_HOST_URL}

        echo "========================================"
        echo "Running SonarQube SAST scan"
        echo "========================================"

        mvn -B -ntp clean verify sonar:sonar \
          -DskipTests \
          -Dskip.npm \
          -Dskip.installnodenpm \
          -Dsonar.projectKey=devsecops-test \
          -Dsonar.projectName=DevSecOpsTest \
          -Dsonar.host.url=${SONAR_HOST_URL} \
          -Dsonar.token=${SONAR_TOKEN}

        EXIT_CODE=$?

        echo "Sonar Maven exit code: $EXIT_CODE"

        if [ "$EXIT_CODE" -ne 0 ]; then
            echo "Sonar scan failed, but pipeline will continue for reporting."
        fi

        exit 0
    '''
}

return this