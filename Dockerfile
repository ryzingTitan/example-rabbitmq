FROM gradle:8.1.1-jdk17-alpine as build

# build project within temporary Docker image
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar -info -x addKtlintFormatGitPreCommitHook

# build final Docker image using output from build image above
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/example-rabbitmq-1.0.0.jar /app/example-rabbitmq-1.0.0.jar
WORKDIR /app

CMD ["java", "-jar", "example-rabbitmq-1.0.0.jar"]