# 使用官方的 OpenJDK 21 基础镜像
FROM openjdk:21-jdk-slim

ARG JAR_FILE
# 设置工作目录
WORKDIR /app

# 复制项目的 JAR 文件到容器中
COPY ${JAR_FILE} /app/app.jar

# 暴露应用程序的端口，这里假设应用程序使用 8080 端口
EXPOSE 8080

# 定义容器启动时执行的命令
CMD ["java", "-jar", "/app/app.jar"]