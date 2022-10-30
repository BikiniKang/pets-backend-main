FROM openjdk:17-oracle
EXPOSE 8080
ADD target/petpocket-backend.jar petpocket-backend.jar
ENTRYPOINT ["java", "-jar", "/petpocket-backend.jar"]