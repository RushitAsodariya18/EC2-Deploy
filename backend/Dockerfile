FROM node:18-alpine

# Set build arguments for Infisical
ARG INFISICAL_TOKEN
ARG PROJECT_ID
ARG INFISICAL_API_URL

# Install curl and Infisical CLI
RUN apk add --no-cache bash curl && \
    curl -1sLf 'https://dl.cloudsmith.io/public/infisical/infisical-cli/setup.alpine.sh' | bash && \
    apk add infisical

WORKDIR /app

COPY package.json .

RUN npm install --omit=dev

COPY . .

# Export secrets to .env file at build time
RUN infisical export \
    --env=dev \
    --projectId=${PROJECT_ID} \
    --token=${INFISICAL_TOKEN} \
    --domain=${INFISICAL_API_URL} \
    --format=dotenv > .env && cat .env

EXPOSE 3000

# Start the app (secrets are already in .env)
CMD ["node", "app.js"]