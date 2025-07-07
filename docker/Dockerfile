# ============= Stage 1: Builder stage =============
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy application files
COPY @hump.name@-@editionName@-@project.version@ .

# Fix permissions for startup scripts
RUN chmod +x startup-gm.sh shutdown-gm.sh

# ============= Stage 2: Final image =============
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="AngusGM Team"

# Set environment variables (default port 8802)
ENV GM_PORT=8802

# Define application directory and create non-root user
ENV APP_HOME=/opt/AngusGM
RUN adduser -D angus && \
    mkdir -p ${APP_HOME} && \
    chown -R angus:angus ${APP_HOME}

# Copy application files from builder stage
COPY --from=builder --chown=angus:angus /app ${APP_HOME}

# Set working directory and default user
WORKDIR ${APP_HOME}
USER angus

# Create runtime directories (ensure they exist but empty)
RUN mkdir -p data logs tmp

# Define volume mount points (for host directory mapping)
VOLUME ["${APP_HOME}/conf", "${APP_HOME}/data", "${APP_HOME}/logs", "${APP_HOME}/plugins", "${APP_HOME}/tmp"]

# Expose application port
EXPOSE ${GM_PORT}

# Health check configuration
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:${GM_PORT}/actuator/health || exit 1

# Application entry point
CMD ["./startup-gm.sh", "--daemon=false"]
