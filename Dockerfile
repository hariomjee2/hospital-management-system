# Stage 1: Build JAR & Extract Layers using Eclipse Temurin JDK 21 Alpine
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper & POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code and build production package
COPY src ./src
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests -B

# Extract Spring Boot layers for production container optimization
RUN java -Djarmode=layertools -jar target/backend-0.0.1-SNAPSHOT.jar extract --destination target/extracted

# Stage 2: Minimal Runtime JRE 21 Alpine Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for production security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy extracted Spring Boot layers
COPY --from=builder /app/target/extracted/dependencies/ ./
COPY --from=builder /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/target/extracted/application/ ./

# Grant non-root user full ownership of app files
RUN chown -R appuser:appgroup /app
USER appuser

# Render sets $PORT dynamically at runtime; default to 8080
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "org.springframework.boot.loader.launch.JarLauncher"]
