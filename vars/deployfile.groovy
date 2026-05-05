def call(String COMMIT_HASH) {
    def vars = variables()
    def infisicalUrl = vars.INFISICAL_API_URL
    
    withCredentials([
        usernamePassword(credentialsId: 'docker-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS'),
        string(credentialsId: 'infisical-token', variable: 'INFISICAL_TOKEN')
    ]) {
        script {
            // Fetch secrets to inject using the static token
            env.SECRETS = sh(
                script: """
                    infisical export \\
                        --env=${vars.INFISICAL_ENV} \\
                        --projectId=${vars.PROJECT_ID} \\
                        --token=${env.INFISICAL_TOKEN} \\
                        --domain=${infisicalUrl} \\
                        --format=dotenv
                """,
                returnStdout: true
            ).trim()
        }

        sshagent(credentials: ['ec2-arshik-key']) {
            sh """
                ssh -o StrictHostKeyChecking=no ${vars.EC2_USER}@${vars.EC2_HOST} "
                    set -e
                    mkdir -p ${vars.TARGET_DIR}
                    echo '${DOCKER_PASS}' | docker login ${vars.REGISTRY} -u '${DOCKER_USER}' --password-stdin
                    
                    docker pull ${vars.REGISTRY}/${vars.NAMESPACE}/multistage-app-backend:${COMMIT_HASH}
                    docker pull ${vars.REGISTRY}/${vars.NAMESPACE}/multistage-app-frontend:${COMMIT_HASH}
                    
                    cd ${vars.TARGET_DIR}

                    # Inject variables directly into the shell for Docker Compose
                    export COMMIT_HASH=${COMMIT_HASH}
                    export INFISICAL_TOKEN=${env.INFISICAL_TOKEN}
                    export PROJECT_ID=${vars.PROJECT_ID}
                    export INFISICAL_API_URL=${vars.INFISICAL_API_URL}

                    # Inject all secrets fetched from Infisical
                    ${env.SECRETS.split('\n').collect { "export ${it}" }.join('\n                    ')}

                    docker compose up -d 
                "
            """
        }
    }
}
