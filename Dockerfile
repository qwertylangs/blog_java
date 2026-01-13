
FROM maven:3.9.12-eclipse-temurin-21-alpine AS builder

LABEL authors="volko"

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests




FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080


ENTRYPOINT ["java", "-jar","app.jar"]