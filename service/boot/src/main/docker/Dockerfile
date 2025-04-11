# Use official Eclipse Temurin JRE base image (LTS version)
FROM eclipse-temurin:17-jre-jammy

# Set environment variables
ENV TZ=Asia/Shanghai \
    APP_USER=appuser \
    APP_HOME=/app \
    PORT=1806

# Configure timezone and create application user
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    useradd -d ${APP_HOME} -s /bin/bash -u 1001 ${APP_USER}

# Set working directory and copy application JAR
WORKDIR ${APP_HOME}
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Set file permissions and switch to non-root user
RUN chown -R ${APP_USER}:${APP_USER} ${APP_HOME}
USER ${APP_USER}

# Configure health check (Spring Boot Actuator endpoint)
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -sf http://localhost:${PORT}/actuator/health || exit 1

# Optimized JVM runtime arguments
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
