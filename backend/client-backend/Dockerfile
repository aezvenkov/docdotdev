# base image
FROM openjdk:19-oracle

# set working directory
WORKDIR /app

# copy JAR file to container
COPY target/client-backend-0.0.1-SNAPSHOT.jar /app/client-backend-0.0.1-SNAPSHOT.jar

# start Spring Boot application
CMD ["java", "-jar", "client-backend-0.0.1-SNAPSHOT.jar"]
