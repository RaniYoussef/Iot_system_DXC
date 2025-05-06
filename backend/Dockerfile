# === Stage 1: Build the app ===
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# === Stage 2: Run the jar ===
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]