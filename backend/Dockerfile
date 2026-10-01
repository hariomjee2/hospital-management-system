# Stage 1: Build JAR using Eclipse Temurin JDK 21 Alpine
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper & POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code and build production package
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Stage 2: Minimal Runtime JRE 21 Alpine Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for production security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Render sets $PORT dynamically at runtime; default to 8080
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
