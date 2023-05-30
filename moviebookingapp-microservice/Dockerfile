FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} movieapp.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/movieapp.jar"]