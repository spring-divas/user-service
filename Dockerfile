FROM gradle:9.7.1-jdk25-alpine AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle bootJar --no-daemon -x test

FROM gcr.io/distroless/java25-debian13:nonroot
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
USER 10001:10001
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]