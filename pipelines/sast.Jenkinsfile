pipeline {

    // Exécuter le pipeline sur l'agent Jenkins Dockerisé
    agent {
        label 'docker-agent'
    }

    // Éviter le checkout automatique du repo pipeline
    options {
        skipDefaultCheckout(true)
    }

    // Variables globales utilisées dans les scripts
    environment {
        // Token SonarQube stocké dans Jenkins Credentials
        SONAR_TOKEN = credentials('sonar-token')

        // URL SonarQube via le nom du service Docker (portable)
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {

        // Vérifier l'environnement de l'agent Jenkins
        stage('Check Environment') {
            steps {
                script {
                    def common = load "${WORKSPACE}/vars/common.groovy"
                    common.printBanner("Checking SAST agent environment")
                    common.printCurrentEnvironment()
                }
            }
        }

        // Cloner le dépôt de l'application à analyser
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

        // Lancer l'analyse SonarQube
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

        // Lancer l'analyse Gitleaks
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

        // Générer le rapport SAST final
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

        // Archiver les rapports générés dans Jenkins
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