FROM openjdk:8-jre
MAINTAINER Dragan Ljubojevic <dragan.ljubojevic@gmail.com>
ADD ./target/resultcapture-service.jar /app/
ARG java_opts
ENV JAVA_OPTS $java_opts
CMD ["sh", "-c", "java -jar $JAVA_OPTS /app/resultcapture-service.jar"]

EXPOSE 7979