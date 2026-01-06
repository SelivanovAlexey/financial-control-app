FROM eclipse-temurin:21-alpine AS builder-base
FROM eclipse-temurin:21-jre-alpine AS runtime-base

FROM builder-base AS deps
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw ./
RUN chmod +x mvnw

COPY pom.xml ./
COPY core/pom.xml ./core/
COPY dependency-bom/pom.xml ./dependency-bom/
COPY migration/pom.xml ./migration/
RUN ./mvnw dependency:go-offline

FROM builder-base AS builder
WORKDIR /app

COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app/.mvn .mvn
COPY --from=deps /app/mvnw ./
COPY . .

RUN ./mvnw clean package

FROM runtime-base AS runner
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder --chown=appuser:appgroup /app/core/target/*.jar financial-app.jar

EXPOSE 8484
ENTRYPOINT ["java", "-jar", "financial-app.jar"]