pipeline {

    agent {
        label 'docker-agent'
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {

        stage('Check Environment') {
            steps {
                script {
                    def common = load "${WORKSPACE}/vars/common.groovy"
                    common.printBanner("Checking SAST agent environment")
                    common.printCurrentEnvironment()
                }
            }
        }

        stage('Checkout Code') {
            steps {
                dir('source-code') {
                    script {
                        def gitCheckout = load "${WORKSPACE}/vars/gitCheckout.groovy"
                        gitCheckout()
                    }
                }
            }
        }

        stage('Run SonarQube Scan') {
            steps {
                dir('source-code') {
                    script {
                        def sonarScan = load "${WORKSPACE}/vars/sonarScan.groovy"
                        sonarScan()
                    }
                }
            }
        }

        stage('Run Gitleaks Scan') {
            steps {
                dir('source-code') {
                    script {
                        def gitleaksScan = load "${WORKSPACE}/vars/gitleaksScan.groovy"
                        gitleaksScan()
                    }
                }
            }
        }

        stage('Generate SAST Report') {
            steps {
                dir('source-code') {
                    script {
                        def generateSastReport = load "${WORKSPACE}/vars/generateSastReport.groovy"
                        generateSastReport()
                    }
                }
            }
        }

        stage('Archive SAST Report') {
            steps {
                script {
                    def common = load "${WORKSPACE}/vars/common.groovy"
                    common.archiveReport('source-code/reports/sast/**')
                }
            }
        }
    }

    post {
        success {
            echo 'SAST pipeline completed successfully.'
        }
        failure {
            echo 'SAST pipeline failed.'
        }
        always {
            echo 'SAST pipeline execution finished.'
        }
    }
}