FROM openjdk:21-jdk

COPY build/libs/community_service.jar /app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]

# 포트 오픈 (Spring + gRPC)
EXPOSE 8093
EXPOSE 50054