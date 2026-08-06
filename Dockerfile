FROM eclipse-temurin:25-jre-alpine-3.23

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

ARG JAR_FILE=target/*.jar

WORKDIR /app

COPY ${JAR_FILE} kelana.jar

ENTRYPOINT ["java","-jar","/app/kelana.jar"]
