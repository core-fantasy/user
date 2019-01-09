FROM openjdk:11-jre-slim
RUN apt-get update && apt-get install curl -y
COPY build/libs/*-all.jar com.corefantasy.user.jar
CMD java ${JAVA_OPTS} -jar com.corefantasy.user.jar