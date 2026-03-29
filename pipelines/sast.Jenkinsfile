pipeline {
    agent any

    stages {
        stage('Load Helpers') {
            steps {
                script {
                    gitHelper = load 'vars/gitCheckout.groovy'
                    sonarHelper = load 'vars/sonarScan.groovy'
                }
            }
        }

        stage('Checkout Code') {
            steps {
                script {
                    gitHelper.call('https://github.com/sindagalai/DevSecOpsTest.git', 'main')
                }
            }
        }

        stage('SAST Scan - SonarQube') {
            steps {
                script {
                    sonarHelper.call()
                }
            }
        }
    }
}