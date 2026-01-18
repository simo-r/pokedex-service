# Stage 1: Build
FROM amazoncorretto:21 AS builder

WORKDIR /app

# Copy the gradle files and source code
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

# Build the application and create bootable JAR
RUN ./gradlew clean bootJar -x test

# Stage 2: Runtime
FROM amazoncorretto:21

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

