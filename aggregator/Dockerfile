FROM eclipse-temurin:21-jre
WORKDIR /app

COPY build/libs/aggregator.jar aggregator.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar aggregator.jar"]

