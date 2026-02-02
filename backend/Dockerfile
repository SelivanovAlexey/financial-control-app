FROM maven:3.9.12-eclipse-temurin-21-alpine AS builder-base
FROM eclipse-temurin:21-jre-alpine AS runtime-base

FROM builder-base AS deps
WORKDIR /app

COPY pom.xml ./
COPY core/pom.xml ./core/
COPY dependency-bom/pom.xml ./dependency-bom/
COPY migration/pom.xml ./migration/
RUN mvn dependency:go-offline

FROM builder-base AS builder
WORKDIR /app

COPY --from=deps /root/.m2 /root/.m2
COPY pom.xml ./
COPY dependency-bom/ ./dependency-bom/
COPY ./core/ ./core
COPY ./migration/ ./migration

RUN mvn clean package -DskipTests

FROM runtime-base AS runner
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder --chown=appuser:appgroup /app/core/target/*.jar financial-app.jar

EXPOSE 8484
ENTRYPOINT ["java", "-jar", "financial-app.jar"]