FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /opt/app


COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -q -DskipTests package \
 && cp $(ls target/*.jar | grep -v original | head -n 1) target/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8080

COPY --from=builder /opt/app/target/app.jar /opt/app/app.jar
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /opt/app/app.jar"]