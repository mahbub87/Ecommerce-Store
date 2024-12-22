FROM openjdk:17-jdk-alpine
COPY target/Project4413-0.0.1-SNAPSHOT.jar project.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/project.jar"]