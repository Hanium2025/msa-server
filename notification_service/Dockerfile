FROM openjdk:21-jdk

COPY build/libs/notification_service-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

# 포트 오픈 (Spring + gRPC)
EXPOSE 8092
EXPOSE 50053