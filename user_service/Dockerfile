FROM openjdk:21-jdk

COPY build/libs/user_service.jar /app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]

# 포트 오픈 (Spring + gRPC)
EXPOSE 8090
EXPOSE 50051