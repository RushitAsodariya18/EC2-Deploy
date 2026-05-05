# Stage 1: Build the React application
FROM node:18-alpine AS build

ARG INFISICAL_TOKEN
ARG PROJECT_ID  
ARG INFISICAL_API_URL

# Install Infisical
RUN apk add --no-cache bash curl && \
    curl -1sLf 'https://dl.cloudsmith.io/public/infisical/infisical-cli/setup.alpine.sh' | bash && \
    apk add infisical

WORKDIR /app

COPY package.json .
RUN npm install

COPY . .

# Export secrets to .env file at build time
RUN infisical export \
    --env=dev \
    --projectId=${PROJECT_ID} \
    --token=${INFISICAL_TOKEN} \
    --domain=${INFISICAL_API_URL} \
    --format=dotenv > .env

# Build with the secrets
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]