# === STAGE: Angular Development Server with ng serve ===
FROM node:20-alpine

# Set working directory
WORKDIR /app

# Install Angular CLI globally
RUN npm install -g @angular/cli

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install --legacy-peer-deps

# Copy the full Angular source code
COPY . .

# Expose ng serve port
EXPOSE 4200

# Start Angular dev server
CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]
