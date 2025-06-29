FROM openjdk:21-jdk

COPY build/libs/community_service-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

# 포트 오픈 (Spring + gRPC)
EXPOSE 8093
EXPOSE 50054