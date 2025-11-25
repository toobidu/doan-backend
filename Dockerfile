# --- STAGE 1: BUILD (Dùng Java 21) ---
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- STAGE 2: RUN (Dùng Java 21) ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN apk add --no-cache tzdata
ENV TZ=Asia/Ho_Chi_Minh

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080 9093
ENTRYPOINT ["java", "-jar", "app.jar"]