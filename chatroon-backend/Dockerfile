FROM eclipse-temurin:17-jdk-alpine
ENV TZ=Asia/Shanghai
VOLUME /tmp
ARG JAR_FILE
ADD target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]