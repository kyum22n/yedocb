# Render는 Java를 네이티브 런타임으로 지원하지 않아 Docker 빌드로 배포한다.
# (docs/deployment-migration.md §4 참고)

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew
COPY src src
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/yedocb-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
