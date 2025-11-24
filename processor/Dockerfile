FROM eclipse-temurin:21-jre
WORKDIR /app

COPY build/libs/processor.jar processor.jar

# jit kinda messes up timings sometimes.
# to show performance increase with jit enabled much bigger text is needed (more than 100-300mb).
ENV JAVA_OPTS="-Xint"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar processor.jar"]
