FROM openjdk:21-jdk

COPY build/libs/notification_service.jar /app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]

# 포트 오픈 (Spring + gRPC)
EXPOSE 8092
EXPOSE 50053