#!/bin/sh

# ---------------------------------------------------------------------------
# Jenkins CI/CD Pipeline Script for AngusGM Project.
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

# Global Variables
SERVICE_DIR="AngusGM/service"
WEB_DIR="AngusGM/web"

REMOTE_SERVICE_DIR="/data/app/AngusGM"
REMOTE_SERVICE_PLUGINS_DIR_NAME="plugins"
REMOTE_SERVICE_PLUGINS_DIR="${REMOTE_SERVICE_DIR}/${REMOTE_SERVICE_PLUGINS_DIR_NAME}"
REMOTE_STATIC_DIR_NAME="statics"
REMOTE_STATIC_DIR="${REMOTE_SERVICE_DIR}/${REMOTE_STATIC_DIR_NAME}"

NGINX_CONFIG_DIR="/opt/required/nginx/conf_ext/"

# Validate input parameters
validate_parameters() {
  # Validate mandatory parameters
  if [ -z "$module" ] || [ -z "$env" ] || [ -z "$editionType" ]; then
    echo "ERROR: Missing required parameters (module, env, editionType)"
    exit 1
  fi

  # Validate editionType and env compatibility
  case "$editionType" in
    edition.cloudService)
      case "$env" in
        env.local|env.dev|env.prod) ;;
        *) echo "ERROR: Cloud edition requires env.local/dev/prod"; exit 1 ;;
      esac ;;
    edition.community|edition.enterprise|edition.datacenter)
      if [ "$env" != "env.priv" ]; then
        echo "ERROR: Private edition requires env.priv"; exit 1
      fi ;;
    *) echo "ERROR: Invalid editionType"; exit 1 ;;
  esac

  # Validate module values
  case "$module" in
    service|web,web,service) ;;
    *) echo "ERROR: Invalid module value"; exit 1 ;;
  esac
}

# Check and clean environment
prepare_environment() {
  echo "INFO: Preparing build environment..."

  # Load system profile for environment variables
  if [ -f "/etc/profile" ]; then
    echo "INFO: Loading system environment variables"
    . /etc/profile
  fi

  if echo "$module" | grep -q "web"; then
    echo "INFO: Checking Node.js environment"
    if ! command -v node >/dev/null || ! command -v npm >/dev/null; then
      echo "ERROR: Node.js/npm not found"; exit 1
    fi
    echo "INFO: Cleaning npm cache"
    npm cache clean --force
  fi

  if echo "$module" | grep -q "service"; then
    echo "INFO: Checking Java/Maven environment"
    if ! command -v java >/dev/null || ! command -v mvn >/dev/null; then
      echo "ERROR: Java/Maven not found"; exit 1
    fi

    echo "INFO: Cleaning Maven repository"
    if [ -n "$MAVEN_HOME" ]; then
      CLEAR_MAVEN_REPO="${MAVEN_HOME}/repository/cloud/xcan"
    else
      CLEAR_MAVEN_REPO="${HOME}/.m2/repository/cloud/xcan"
    fi

    echo "INFO: Cleaning Maven repository at ${CLEAR_MAVEN_REPO}"
    rm -rf "${CLEAR_MAVEN_REPO}"/*
  fi
}

# Clone repository
clone_repository() {
  echo "INFO: Cloning repository branch: ${gitBranch:-main}"
  GIT_URL="https://github.com/xcancloud/AngusGM.git"

  if [ -n "$gitCredential" ]; then
    GIT_URL="https://${gitCredential}@github.com/xcancloud/AngusGM.git"
  fi

  git clone -b "${gitBranch:-main}" "$GIT_URL" || {
    echo "ERROR: Failed to clone repository"; exit 1
  }
}

# Build service module
maven_build () {
  echo "INFO: mvn build start"
  mvn -B -e -U clean package -Dmaven.test.skip=true -s ${MAVEN_HOME}/conf/xcan_repo_settings.xml -f pom.xml -P${env},${editionType},${dbType}
  if [ $? -ne 0 ]; then
    error "ERROR: mvn build failed"
    exit 1
  fi
  echo "INFO: mvn build end"
}

# Deploy service module
deploy_service() {
  echo "INFO: Deploying service module to ${host}"
  ssh "$host" "cd ${REMOTE_SERVICE_DIR} && sh shutdown-gm.sh" || {
    echo "WARN: Failed to stop service, proceeding anyway"
  }
  ssh "$host" "cd ${REMOTE_SERVICE_DIR} && find . -mindepth 1 -maxdepth 1 -not \( -name ${REMOTE_STATIC_DIR_NAME} -o -name ".*" \) -exec rm -rf {} +" || {
    echo "ERROR: Failed to clean service directory"; exit 1
  }
  scp -r "${SERVICE_DIR}/boot/target"/* "${host}:${REMOTE_SERVICE_DIR}/" || {
    echo "ERROR: Failed to copy service files"; exit 1
  }
  scp -r "${SERVICE_DIR}/boot/extension/dist"/* "${host}:${REMOTE_SERVICE_PLUGINS_DIR}/" || {
    echo "ERROR: Failed to copy plugin files"; exit 1
  }
  ssh "$host" "cd ${REMOTE_SERVICE_DIR} && mkdir -p conf && mv classes/spring-logback.xml conf/gm-logback.xml" || {
    echo "ERROR: Failed to rename logback file"; exit 1
  }
  ssh "$host" "cd ${REMOTE_SERVICE_DIR} && sh set-opts.sh ${host} && sh startup-gm.sh" || {
    echo "ERROR: Failed to start service"; exit 1
  }
  ssh "$host" "cd ${REMOTE_SERVICE_DIR} && sh check-health.sh" || {
    echo "ERROR: Service health check failed"; exit 1
  }
}

# Build web module
npm_build () {
  echo "INFO: npm install start"
  npm install --omit=optional --legacy-peer-deps

  if [ $? -ne 0 ]; then
    echo "ERROR: 'npm install --no-optional --legacy-peer-deps' failed, exiting script"
    exit 1
  fi
  echo "INFO: npm install end"

  echo "INFO: npm run build:env start"
  editionType0="${editionType##*.}"; editionType0="${editionType0^^}"
  npm run build:env -- --env=${env##*.} --edition_type=${editionType0}
  if [ $? -ne 0 ]; then
    echo "ERROR: 'npm run build:env' failed, exiting script"
    exit 1
  fi
  echo "INFO: npm build end"
}

# Deploy web module
deploy_web() {
  echo "INFO: Deploying web module to ${host}"
  ssh "$host" "rm -rf ${REMOTE_STATIC_DIR}/*" || {
    echo "ERROR: Failed to clean static directory"; exit 1
  }
  scp -r "${WEB_DIR}/dist"/* "${host}:${REMOTE_STATIC_DIR}/" || {
    echo "ERROR: Failed to copy web assets"; exit 1
  }
  nginxFileName="dist/nginx_${env##*.}_gm.conf"
  scp -p ${nginxFileName} "${host}:${NGINX_CONFIG_DIR}/" || {
    echo "ERROR: Failed to copy web assets"; exit 1
  }
  ssh "$host" "nginx -s reload" || {
    echo "ERROR: Failed to reload nginx"; exit 1
  }
}

# Main execution flow
while [ $# -gt 0 ]; do
  case "$1" in
    --module) module="$2"; shift ;;
    --env) env="$2"; shift ;;
    --editionType) editionType="$2"; shift ;;
    --hosts) hosts="$2"; shift ;;
    --dbType) dbType="${2:-db.mysql}"; shift ;;
    --gitBranch) gitBranch="$2"; shift ;;
    --gitCredential) gitCredential="$2"; shift ;;
    *) echo "WARN: Unknown parameter $1"; shift ;;
  esac
  shift
done

# Step 1: Parameter validation
validate_parameters

# Step 2: Environment preparation
prepare_environment

# Step 3: CI Phase
clone_repository

if echo "$module" | grep -q "service"; then
  echo "INFO: Building service module"
  cd "$SERVICE_DIR" || exit 1
  maven_build || {
    echo "ERROR: Service build failed"; exit 1
  }
  cd ../..
fi

if echo "$module" | grep -q "web"; then
  echo "INFO: Building web module"
  cd "$WEB_DIR" || exit 1
  npm_build || {
    echo "ERROR: Web build failed"; exit 1
  }
  cd ../..
fi

# Step 4: CD Phase
if [ -n "$hosts" ]; then
  echo "INFO: Starting deployment to hosts: ${hosts}"
  IFS=',' read -ra HOST_LIST <<< "$hosts"
  for host in "${HOST_LIST[@]}"; do
    if echo "$module" | grep -q "web"; then
      deploy_web
    fi
    if echo "$module" | grep -q "service"; then
      deploy_service
    fi
  done
else
  echo "INFO: No hosts specified, skipping deployment"
fi

echo "SUCCESS: CI/CD pipeline completed successfully"
exit 0
