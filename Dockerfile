FROM openjdk:11-jdk-slim
MAINTAINER chelcovdanik@gmail.com

RUN mkdir -p /usr/src/app/
WORKDIR /usr/src/app/

COPY . /usr/src/app

EXPOSE 8080
ENTRYPOINT ["./gradlew", "bootRun"]