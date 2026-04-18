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

        if [ "${PROJECT_TYPE_ENV}" = "java" ] || [ "${PROJECT_TYPE_ENV}" = "fullstack" ]; then
            mvn -B -ntp clean verify sonar:sonar \
              -DskipTests \
              -Dskip.npm \
              -Dskip.installnodenpm \
              -Dsonar.projectKey=${SONAR_PROJECT_KEY_ENV} \
              -Dsonar.projectName=${SONAR_PROJECT_NAME_ENV} \
              -Dsonar.host.url=${SONAR_HOST_URL} \
              -Dsonar.token=${SONAR_TOKEN}
        else
            echo "PROJECT_TYPE_ENV=${PROJECT_TYPE_ENV} not yet using Maven Sonar flow."
            echo "Skipping Maven-based Sonar scan."
        fi

        EXIT_CODE=$?

        echo "Sonar Maven exit code: $EXIT_CODE"

        if [ "$EXIT_CODE" -ne 0 ]; then
            echo "Sonar scan failed, but pipeline will continue for reporting."
        fi

        exit 0
    '''
}

return this