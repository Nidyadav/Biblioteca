FROM openjdk:8-jdk-alpine
WORKDIR ./
COPY build/libs/biblioteca-0.0.1-SNAPSHOT.jar biblioteca.jar
ENTRYPOINT [“java”, “-Xmx512m”, “-Xms256m”,”-jar”,”biblioteca.jar”]