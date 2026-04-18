pipeline {

    agent {
        label 'docker-agent'
    }

    options {
        timeout(time: 45, unit: 'MINUTES')
        timestamps()
    }

    parameters {
        string(name: 'REPO_URL', defaultValue: 'https://github.com/sindagalai/DevSecOpsTest.git', description: 'Repository URL to scan')
        string(name: 'REPO_BRANCH', defaultValue: 'main', description: 'Branch to scan')
        choice(name: 'PROJECT_TYPE', choices: ['java', 'fullstack', 'node', 'python', 'generic'], description: 'Project type')
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'devsecops-test', description: 'SonarQube project key')
        string(name: 'SONAR_PROJECT_NAME', defaultValue: 'DevSecOpsTest', description: 'SonarQube project name')
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        SONAR_HOST_URL = 'http://sonarqube:9000'
        MAVEN_OPTS = '-Xms256m -Xmx1024m'

        REPO_URL_ENV = "${params.REPO_URL}"
        REPO_BRANCH_ENV = "${params.REPO_BRANCH}"
        PROJECT_TYPE_ENV = "${params.PROJECT_TYPE}"
        SONAR_PROJECT_KEY_ENV = "${params.SONAR_PROJECT_KEY}"
        SONAR_PROJECT_NAME_ENV = "${params.SONAR_PROJECT_NAME}"
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

        stage('Run Gitleaks Scan') {
            steps {
                dir('source-code') {
                    script {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            def gitleaksScan = load "${WORKSPACE}/vars/gitleaksScan.groovy"
                            gitleaksScan()
                        }
                    }
                }
            }
        }

        stage('Run SonarQube Scan') {
            steps {
                dir('source-code') {
                    script {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            def sonarScan = load "${WORKSPACE}/vars/sonarScan.groovy"
                            sonarScan()
                        }
                    }
                }
            }
        }

        stage('Generate SAST Report') {
            steps {
                dir('source-code') {
                    script {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            def generateSastReport = load "${WORKSPACE}/vars/generateSastReport.groovy"
                            generateSastReport()
                        }
                    }
                }
            }
        }

        stage('Archive SAST Report') {
            steps {
                archiveArtifacts artifacts: 'source-code/reports/sast/**', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'SAST pipeline completed successfully.'
        }
        unstable {
            echo 'SAST pipeline completed with warnings.'
        }
        failure {
            echo 'SAST pipeline failed.'
        }
        always {
            echo 'SAST pipeline execution finished.'
        }
    }
}