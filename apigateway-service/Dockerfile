FROM openjdk:21-jdk

COPY build/libs/apigateway-service-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java","-Dfile.encoding=UTF-8", "-jar", "/app.jar"]

# 포트 오픈 (Spring + gRPC)
EXPOSE 8000