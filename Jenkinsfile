@Library('training-lib') _

def COMMIT_HASH = ''

pipeline {
    agent any

    environment {
        DOCKER_CREDS = credentials('docker-cred')
    }

    stages {
        stage('Clone code') {
            steps {
                script {
                    clonecode('git@github.com:RushitAsodariya18/EC2-Deploy.git', 'rushit_work', 'github-ssh-key')
                    
                    COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    echo "Using commit hash as image tag: ${COMMIT_HASH}"
                }
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                script {
                    buildandpush(COMMIT_HASH)
                }
            }
        }

        stage('Transfer Files') {
            steps {
                transferfile()
            }
        }

        stage('Deploy on EC2') {
            steps {
                script {
                    deployfile(COMMIT_HASH)
                }
            }
        }
    }
}
