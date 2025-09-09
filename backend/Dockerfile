# Build stage
FROM clojure:temurin-21-tools-deps AS build
WORKDIR /app

COPY deps.edn /app/
RUN clojure -P # cache deps
COPY . /app
RUN clojure -T:build uber

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/app.jar /app/app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -jar /app/app.jar"]