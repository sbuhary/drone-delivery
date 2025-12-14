## Multi-stage build to keep runtime image slim.
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

# Cache dependencies first.
COPY pom.xml .
RUN mvn -B -q -DskipTests -Djava.version=21 dependency:go-offline

# Copy sources and build the application.
COPY src ./src
RUN mvn -B -q -DskipTests -Djava.version=21 clean package

# Runtime image.
FROM eclipse-temurin:25-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /workspace/target/drone-delivery-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# Default entrypoint; override JAVA_OPTS to tune memory/GC if needed.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
