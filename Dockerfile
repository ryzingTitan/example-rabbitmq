FROM gradle:7.6-jdk17-alpine as build

# build project within temporary Docker image
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar -info

# build final Docker image using output from build image above
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/example-rabbitmq-1.0.0.jar /app/example-rabbitmq-1.0.0.jar
WORKDIR /app

CMD ["java", "-jar", "example-rabbitmq-1.0.0.jar"]