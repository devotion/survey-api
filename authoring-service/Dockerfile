FROM openjdk:8-jre
ADD ./target/authoring-service.jar /app/
ARG java_opts
ENV JAVA_OPTS $java_opts
CMD ["sh", "-c", "java -jar $JAVA_OPTS /app/authoring-service.jar"]

EXPOSE 7979