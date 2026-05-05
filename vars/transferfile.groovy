def call() {
    def vars = variables()

    echo "EC2 User: ${vars.EC2_USER}"
    echo "EC2 Host: ${vars.EC2_HOST}"
    echo "Target Dir: ${vars.TARGET_DIR}"

    sshagent(['ec2-arshik-key']) {
        sh """
          ssh -o StrictHostKeyChecking=no \
            ${vars.EC2_USER}@${vars.EC2_HOST} \
            'mkdir -p ${vars.TARGET_DIR}'

          scp -o StrictHostKeyChecking=no \
            rushit_asodariya/Day-6/compose-02-finished/docker-compose.yml \
            ${vars.EC2_USER}@${vars.EC2_HOST}:${vars.TARGET_DIR}/

          scp -r -o StrictHostKeyChecking=no \
            rushit_asodariya/Day-6/compose-02-finished/nginx \
            ${vars.EC2_USER}@${vars.EC2_HOST}:${vars.TARGET_DIR}/
        """
    }
}
