## Build stage: compiles and produces the bootJar
FROM gradle:8.14.0-jdk21 AS build

WORKDIR /workspace

# Leverage Docker layer caching for dependencies
COPY build.gradle settings.gradle gradle.properties* /workspace/
COPY src /workspace/src

# Build Spring Boot fat JAR (tests can be re-enabled by removing -x test)
RUN gradle --no-daemon clean bootJar -x test

## Runtime stage: minimal JRE image running the built jar
FROM eclipse-temurin:21-jre

WORKDIR /app

ENV JAVA_OPTS=""
ENV SERVER_PORT=8080

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

# Optional container-friendly defaults; can be overridden at runtime
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$SERVER_PORT -jar /app/app.jar"]

