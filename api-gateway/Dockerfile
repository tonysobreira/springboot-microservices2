FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]