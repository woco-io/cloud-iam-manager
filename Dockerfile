FROM eclipse-temurin:21-jammy as build

ARG appName=cloud-iam-manager
ARG appVersion=1.0.0

USER root

WORKDIR /tmp/build

RUN apt-get update && \
    apt-get install -y \
    maven

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jammy

ENV appName cloud-iam-manager
ENV appVersion 1.0.0

RUN useradd -m -u 1000 java

WORKDIR /app

COPY --from=build /tmp/build/target/${appName}-${appVersion}.jar /app/app.jar

RUN chown -R java:java /app && \
    chmod +x /app/app.jar

USER 1000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
