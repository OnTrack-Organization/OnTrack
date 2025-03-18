FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
