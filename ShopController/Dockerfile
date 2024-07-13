# Dockerfile for ShopController

# Use an appropriate base image with Java and Maven pre-installed
FROM maven:3.8.4-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files into the container
COPY pom.xml .
COPY src ./src

# Run Maven build commands to compile and package the application
RUN mvn clean install -DskipTests

# Use JDK 17
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file into the container
COPY --from=builder /app/target/ShopController-0.0.1-SNAPSHOT.jar /app/ShopController-0.0.1-SNAPSHOT.jar

COPY src/main/resources/application.properties /app

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Define the command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "ShopController-0.0.1-SNAPSHOT.jar"]