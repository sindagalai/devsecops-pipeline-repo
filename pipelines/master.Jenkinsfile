pipeline {
    
    agent {
        docker {
            image 'jenkins/inbound-agent:3261.v9c670a_4748a_9-1'  // Utilise l'image Docker de ton agent
            args '-v /var/run/docker.sock:/var/run/docker.sock'  // Permet à Docker de s'exécuter à l'intérieur du conteneur
        }
    }




    parameters {
        choice(
            name: 'SCAN_TYPE',
            choices: ['SAST', 'SCA', 'DAST', 'ALL'],
            description: 'Choisir le type de scan à exécuter'
        )
    }

    stages {
        stage('Initialize') {
            steps {
                echo "Master pipeline started"
                echo "Selected scan type: ${params.SCAN_TYPE}"
            }
        }

        stage('Trigger SAST Pipeline') {
            when {
                anyOf {
                    expression { params.SCAN_TYPE == 'SAST' }
                    expression { params.SCAN_TYPE == 'ALL' }
                }
            }
            steps {
                echo 'Triggering sast-pipeline...'
                build job: 'sast-pipeline', wait: true
            }
        }

        stage('Trigger SCA Pipeline') {
            when {
                anyOf {
                    expression { params.SCAN_TYPE == 'SCA' }
                    expression { params.SCAN_TYPE == 'ALL' }
                }
            }
            steps {
                echo 'Triggering sca-pipeline...'
                build job: 'sca-pipeline', wait: true
            }
        }

        stage('Trigger DAST Pipeline') {
            when {
                anyOf {
                    expression { params.SCAN_TYPE == 'DAST' }
                    expression { params.SCAN_TYPE == 'ALL' }
                }
            }
            steps {
                echo 'Triggering dast-pipeline...'
                build job: 'dast-pipeline', wait: true
            }
        }
    }

    post {
        success {
            echo 'Master pipeline finished successfully.'
        }
        failure {
            echo 'Master pipeline failed.'
        }
    }
}