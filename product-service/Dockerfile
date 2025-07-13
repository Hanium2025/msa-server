FROM openjdk:21-jdk

COPY build/libs/product_service.jar /app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]

EXPOSE 8091
EXPOSE 50052