FROM openjdk:21-jdk

COPY build/libs/config-service-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]

EXPOSE 8888