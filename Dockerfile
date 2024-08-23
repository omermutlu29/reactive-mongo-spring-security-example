FROM maven:3.9.9-amazoncorretto-17 AS build

COPY pom.xml ./
COPY .mvn .mvn

COPY src src

RUN mvn clean install -DskipTests

FROM amazoncorretto:17

WORKDIR /

COPY --from=build target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]