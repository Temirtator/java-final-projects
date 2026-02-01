FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
COPY service-1/pom.xml /app/service-1/pom.xml
COPY service-2/pom.xml /app/service-2/pom.xml
RUN mvn -f /app/service-2/pom.xml -B -q -DskipTests dependency:go-offline
COPY service-2/src /app/service-2/src
RUN mvn -f /app/service-2/pom.xml -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
COPY --from=build /app/service-2/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
