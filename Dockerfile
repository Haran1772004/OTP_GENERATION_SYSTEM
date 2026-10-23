
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY common/pom.xml common/
COPY otp-service/pom.xml otp-service/
COPY notification-service/pom.xml notification-service/
COPY common/src common/src
COPY otp-service/src otp-service/src
COPY notification-service/src notification-service/src
RUN mvn -q -pl otp-service,notification-service -am package -DskipTests


FROM eclipse-temurin:17-jre AS otp-service
WORKDIR /app
COPY --from=build /app/otp-service/target/otp-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


FROM eclipse-temurin:17-jre AS notification-service
WORKDIR /app
COPY --from=build /app/notification-service/target/notification-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]