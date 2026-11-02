# syntax=docker/dockerfile:1

### Stage 1: build ###
FROM maven:3.10.0-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

### Stage 2: runtime ###
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --no-create-home --shell /usr/sbin/nologin app
USER app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
