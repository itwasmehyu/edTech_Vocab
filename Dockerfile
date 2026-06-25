# Bước 1: Dùng Image Maven chạy Java 21 để build file JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file cấu hình và mã nguồn vào container
COPY pom.xml .
COPY src ./src

# Tiến hành build dự án, bỏ qua chạy thử test để tăng tốc độ
RUN mvn clean package -DskipTests

# Bước 2: Dùng Image JDK 21 nhỏ gọn để chạy ứng dụng thương mại
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file JAR đã build từ bước 1 sang bước 2
COPY --from=build /app/target/*.jar app.jar

# Cổng mạng mà Spring Boot sẽ lắng nghe bên trong Container
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]