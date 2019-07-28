FROM maven:3.6-jdk-8-alpine AS builder
LABEL authors="Ibanga Enoobong <ibangaenoobong@yahoo.com>"
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package

FROM openjdk:8-jre-alpine
COPY --from=builder /app/target/billable-hours-0.0.1-SNAPSHOT.jar /
CMD ["java", "-jar", "/billable-hours-0.0.1-SNAPSHOT.jar"]
