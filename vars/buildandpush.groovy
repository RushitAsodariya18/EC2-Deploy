def call(String COMMIT_HASH) {
    def vars = variables()
    def infisicalUrl = vars.INFISICAL_API_URL
    
    withCredentials([
        usernamePassword(credentialsId: 'docker-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS'),
        string(credentialsId: 'infisical-token', variable: 'INFISICAL_TOKEN')
    ]) {
        // Token is loaded as INFISICAL_TOKEN env var

        sh "echo \$DOCKER_PASS | docker login ${vars.REGISTRY} -u \$DOCKER_USER --password-stdin"

        dir('rushit_asodariya/Day-6/compose-02-finished/backend') {
            sh """
                docker build \\
                    --build-arg INFISICAL_TOKEN=${env.INFISICAL_TOKEN} \\
                    --build-arg PROJECT_ID=${vars.PROJECT_ID} \\
                    --build-arg INFISICAL_API_URL=${infisicalUrl} \\
                    -t ${vars.REGISTRY}/${vars.NAMESPACE}/multistage-app-backend:${COMMIT_HASH} .
                docker push ${vars.REGISTRY}/${vars.NAMESPACE}/multistage-app-backend:${COMMIT_HASH}
            """
        }

        dir('rushit_asodariya/Day-6/compose-02-finished/frontend') {
            sh """
                docker build \\
                    --add-host=host.docker.internal:host-gateway \\
                    --build-arg INFISICAL_TOKEN=${env.INFISICAL_TOKEN} \\
                    --build-arg PROJECT_ID=${vars.PROJECT_ID} \\
                    --build-arg INFISICAL_API_URL=${infisicalUrl} \\
                    -t ${vars.REGISTRY}/${vars.NAMESPACE}/multistage-app-frontend:${COMMIT_HASH} .
                docker push ${vars.REGISTRY}/${vars.NAMESPACE}/multistage-app-frontend:${COMMIT_HASH}
            """
        }
    }
}
