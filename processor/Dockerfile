FROM eclipse-temurin:21-jre
WORKDIR /app

COPY build/libs/processor.jar processor.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar processor.jar"]

