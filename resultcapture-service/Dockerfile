FROM openjdk:8-jre
ADD ./target/resultcapture-service.jar /app/
ARG java_opts
ENV JAVA_OPTS $java_opts
CMD ["sh", "-c", "java -jar $JAVA_OPTS /app/resultcapture-service.jar"]
EXPOSE 7979