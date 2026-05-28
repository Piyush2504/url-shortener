# Build stage
FROM eclipse-temurin:25-jdk-jammy AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -q clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jdk-jammy
WORKDIR /url-shortener
COPY --from=builder /workspace/target/url-shortener.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]