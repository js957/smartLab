FROM openjdk:8
VOLUME /tmp
ADD face_-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8899
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
