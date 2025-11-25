FROM eclipse-temurin:21-jre
WORKDIR /app

COPY build/libs/client.jar client.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar client.jar"]

