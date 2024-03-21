FROM maven:3.6.0 AS build
COPY src /home/app/src
COPY pom.xml /home/app
COPY src/main/resources/bps.json /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:8u212-jre-alpine
COPY --from=build /home/app/target/backendchallenge-*.jar /usr/local/lib/backendchallenge.jar
COPY --from=build /home/app/bps.json /usr/local/lib/bps.json
ENTRYPOINT [ "sh", "-c", "java -jar /usr/local/lib/backendchallenge.jar" ]