# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B || true

COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8080 9093

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

USER spring:spring

ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-XX:+UseG1GC", "-XX:+UseContainerSupport", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
