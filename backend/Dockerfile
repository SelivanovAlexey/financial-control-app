# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
ARG BUILD_PROFILE
ENV BUILD_PROFILE=${BUILD_PROFILE}

WORKDIR /app
COPY . .

RUN mvn -P${BUILD_PROFILE} clean package

# Stage 2: Runtime
FROM eclipse-temurin:21

WORKDIR /app

COPY --from=build /app/core/target/*.jar financial-app.jar
ARG SPRING_PROFILE
ENV SPRING_PROFILE=${SPRING_PROFILE}

EXPOSE 8484

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILE -jar financial-app.jar"]