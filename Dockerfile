## Stage 1: Build the application
#FROM eclipse-temurin:17-jdk-alpine
#WORKDIR /app
#COPY pom.xml .
#COPY mvnw .
#COPY .mvn .mvn
#RUN ./mvnw dependency:go-offline
#COPY src src
#RUN ./mvnw clean package
