https://www.appsdeveloperblog.com/deploying-spring-boot-mongodb-application-with-docker/
/*********************/
FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
/**********************/
/**********************/
spring.data.mongodb.host=host.docker.internal
spring.data.mongodb.port=27017
/**********************/
IMP step::by default docker runs on isolation:
step1: docker network create moviebooking-network
run docker container on same network that created
step2: docker run --network moviebooking-network -d --name auth-app-docker -p 8080:8080 authapp_movie
step3: docker run --network moviebooking-network -d --name movie-app-docker -p 8081:8081 moviebook_app
docker--
step1: mvn clean install --create jar file
step2: docker build -t myfirst_docker_image .
step3: docker pull mongo(optional if locally have this)
step4: docker run -d --name mongo-on-docker -p 27017:27017 mongo
step5: docker run -d --name springapplication-on-docker -p 8080:8080 myfirst_docker_image
//other steps:
docker images
docker stop <containerId>
docker ps
docker container ls -a
docker logs <ContainerID>