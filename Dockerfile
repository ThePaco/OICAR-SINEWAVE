# Stage 1: Build the application using Maven
# Use an official Maven image with a specific JDK version (matching your project's Java version)
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw .
COPY mvnw.cmd .

# Package the application to create the JAR file
# Use -DskipTests to speed up the build if tests are run elsewhere
RUN mvn package -DskipTests -B

# Stage 2: Create the runtime image
# Use a slim OpenJDK runtime image for a smaller final image size
#FROM openjdk:21-jre-slim
FROM eclipse-temurin:21-jre-jammy
# FROM eclipse-temurin:21-jre-alpine # Even smaller, but might have compatibility issues with some native libs

# Set the working directory in the container
WORKDIR /app

# Argument to specify the JAR file name (can be overridden at build time if needed)
ARG JAR_FILE_NAME=sinewaveApp-0.0.1-SNAPSHOT.jar

# Copy the JAR file from the build stage
COPY --from=build /app/target/${JAR_FILE_NAME} app.jar

EXPOSE 8080

# Define environment variables that Render will set.
# These are placeholders; actual values will be set in Render's UI.
# You don't strictly need to define them here with ENV if Render sets them,
# but it can be good for documentation or local Docker runs.
# ENV SPRING_DATASOURCE_URL=""
# ENV SPRING_DATASOURCE_USERNAME=""
# ENV SPRING_DATASOURCE_PASSWORD=""
# ENV JWT_SECRET=""
# ENV JWT_EXPIRATION=""
# ENV JWT_REFRESH_EXPIRATION=""
# ENV SPRING_MAIL_HOST=""
# ENV SPRING_MAIL_PORT=""
# ENV SPRING_MAIL_USERNAME=""
# ENV SPRING_MAIL_PASSWORD=""
# ENV APP_MUSIC_UPLOAD_DIR="/app/music" # Default upload dir inside the container

# Create the music upload directory if it doesn't exist and ensure it's writable
# Render might handle volume mounts differently, but this is good for local Docker.
# For Render, you'll likely use a Persistent Disk for 'music/'.
RUN mkdir -p /app/music && chown -R 1000:1000 /app/music
# The user ID 1000 is common for non-root users in many base images.
# Adjust if your base image uses a different default user or if Render specifies one.

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add a health check (Render can use this)
# HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
#   CMD curl -f http://localhost:8080/actuator/health || exit 1
# (You'd need to add spring-boot-starter-actuator dependency for this to work)