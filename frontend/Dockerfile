### STAGE 1: Build Angular app ###
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install --legacy-peer-deps

# Copy rest of the project and build Angular app
COPY . .
RUN npm run build -- --configuration production --project frontend-temp

### STAGE 2: Serve with NGINX ###
FROM nginx:alpine

# Clean the default nginx site
RUN rm -rf /usr/share/nginx/html/*

# Copy NGINX custom config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built Angular app to NGINX public dir
COPY --from=builder /app/dist/frontend-temp/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
