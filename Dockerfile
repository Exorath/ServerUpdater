FROM maven:3.5-jdk-8
MAINTAINER toonsevrin@gmail.com

COPY . /usr/src/server/
WORKDIR /usr/src/server/
RUN mvn package

ENV PORT 8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/ServerUpdater.jar"]
