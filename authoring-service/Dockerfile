FROM openjdk:9.0.1-11-jre
MAINTAINER Dragan Ljubojevic <dragan.ljubojevic@gmail.com>
ADD ./target/authoring-service.jar /app/
ARG java_opts
ENV JAVA_OPTS $java_opts
CMD ["sh", "-c", "java -jar $JAVA_OPTS /app/authoring-service.jar"]

EXPOSE 7878 7007