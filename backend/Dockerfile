# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
ARG BUILD_ENV
ENV BUILD_ENV=${BUILD_ENV}

WORKDIR /app
COPY . .

RUN mvn -P${BUILD_ENV} clean package

# Stage 2: Runtime
FROM eclipse-temurin:21

WORKDIR /app

COPY --from=build /app/core/target/*.jar financial-app.jar
COPY --from=build /app/entrypoint.sh .
RUN chmod +x entrypoint.sh

EXPOSE 8484
ENTRYPOINT ["./entrypoint.sh"]