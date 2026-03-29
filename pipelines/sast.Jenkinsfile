pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                script {
                    gitCheckout('https://github.com/sindagalai/DevSecOpsTest.git', 'main')
                }
            }
        }

        stage('SAST Scan - SonarQube') {
            steps {
                script {
                    sonarScan()
                }
            }
        }

    }
}