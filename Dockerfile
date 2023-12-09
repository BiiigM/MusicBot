FROM maven:3.8.3-openjdk-17
COPY pom.xml pom.xml
COPY src/ src/
RUN mvn clean package
ENTRYPOINT ["java","-jar","target/MusicBot-v2.0.jar"]