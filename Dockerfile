# Stage 1: Build
FROM gradle:9.4.1-jdk25-alpine AS build
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle bootJar --no-daemon

# Stage 2: Run
FROM amazoncorretto:25.0.2-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]