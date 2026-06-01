FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S gym && adduser -S gym -G gym
USER gym

COPY build/libs/app.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
