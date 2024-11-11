# Base Image
FROM bellsoft/liberica-openjdk-slim:17

# Gradle 빌드 명령어
CMD ["./gradlew", "clean", "build"]

# 임시 디렉토리 설정
VOLUME /tmp

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 경로 설정
ARG JAR_FILE_PATH=build/libs/lifestock-0.0.1-SNAPSHOT.jar

# app.jar라는 이름으로 컨테이너에 추가
COPY ${JAR_FILE_PATH} app.jar

# 포트 설정
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
