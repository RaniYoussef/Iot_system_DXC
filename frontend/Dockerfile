# Stage 1: Build Angular app
FROM node:18-alpine AS build

WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# Stage 2: Serve app with NGINX
FROM nginx:alpine
COPY --from=build /app/dist/frontend-temp /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
