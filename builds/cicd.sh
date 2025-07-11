#!/bin/sh

# ---------------------------------------------------------------------------
# Jenkins CI/CD Pipeline Script for AngusGM Project.
# Usage: sh cicd.sh --module service --env env.dev --editionType edition.cloud_service --hosts 127.0.0.1 --dbType db.mysql
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

# Global Variables
SERVICE_DIR="service"
WEB_DIR="web"

REMOTE_APP_DIR="/data/apps/AngusGM"
REMOTE_APP_PLUGINS_DIR_NAME="plugins"
REMOTE_APP_PLUGINS_DIR="${REMOTE_APP_DIR}/${REMOTE_APP_PLUGINS_DIR_NAME}"
REMOTE_APP_CONF_DIR="/data/apps/conf/gm"

REMOTE_APP_STATIC_DIR_NAME="statics"
REMOTE_APP_STATIC_DIR="${REMOTE_APP_DIR}/${REMOTE_APP_STATIC_DIR_NAME}"
REMOTE_APP_LOGS_DIR_NAME="logs"

NGINX_CONFIG_DIR="/etc/nginx/conf.d"

CLEAR_MAVEN_REPO="/data/repository"

PORT=1806
PRIVATE_PORT=8802

# Validate input parameters
validate_parameters() {
  # Validate mandatory parameters
  if [ -z "$module" ] || [ -z "$env" ] || [ -z "$editionType" ]; then
    echo "ERROR: Missing required parameters (module, env, editionType)"
    exit 1
  fi

  # Validate editionType and env compatibility
  case "$editionType" in
    edition.cloud_service)
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
    service|web|web,service|service,web) ;;
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

    echo "INFO: Cleaning Maven repository at ${CLEAR_MAVEN_REPO}/cloud/xcan/"
    rm -rf "${CLEAR_MAVEN_REPO}"/cloud/xcan/*
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
  mvn -B -e -U clean package -Dmaven.test.skip=true -s ${MAVEN_HOME}/conf/xcan_repo_settings.xml -f pom.xml -P${editionType},${env},${dbType}
  if [ $? -ne 0 ]; then
    echo "ERROR: mvn build failed"
    exit 1
  fi
  echo "INFO: mvn build end"
}

# Deploy service module
deploy_service() {
  echo "INFO: Deploying service module to ${host}"
  ssh "$host" "mkdir -p ${REMOTE_APP_DIR}" || {
    echo "ERROR: Failed to init app directory"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && sh shutdown-gm.sh" || {
    echo "WARN: Failed to stop service, proceeding anyway"
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && find . -mindepth 1 -maxdepth 1 -not \( -name ${REMOTE_APP_LOGS_DIR_NAME} -o -name ${REMOTE_APP_STATIC_DIR_NAME} \) -exec rm -rf {} +" || {
    echo "ERROR: Failed to clean service directory"; exit 1
  }
  scp -rp "${SERVICE_DIR}/boot/target"/* "${host}:${REMOTE_APP_DIR}/" || {
    echo "ERROR: Failed to copy service files"; exit 1
  }
  ssh "$host" "mkdir -p ${REMOTE_APP_PLUGINS_DIR}" || {
    echo "ERROR: Failed to init plugins directory"; exit 1
  }
  scp -rp "${SERVICE_DIR}/extension/dist"/* "${host}:${REMOTE_APP_PLUGINS_DIR}/" || {
    echo "ERROR: Failed to copy plugin files"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && mkdir -p conf && mv classes/spring-logback.xml conf/gm-logback.xml" || {
    echo "ERROR: Failed to rename logback file"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && cp -f ${REMOTE_APP_CONF_DIR}/.*.env conf/" || {
    echo "ERROR: Failed to copy env files"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && sh startup-gm.sh --debug" || {
    echo "ERROR: Failed to start service"; exit 1
  }
  sh builds/check-health.sh ${host} ${PORT} || {
    echo "ERROR: Service health check failed"; exit 1
  }
}

# Deploy private app
deploy_private_edition() {
  echo "INFO: Deploying private edition to ${host}"
  ssh "$host" "mkdir -p ${REMOTE_APP_DIR}" || {
    echo "ERROR: Failed to init app directory"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && sh shutdown-gm.sh" || {
    echo "WARN: Failed to stop app, proceeding anyway"
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && rm -rf *" || {
    echo "ERROR: Failed to clean app directory"; exit 1
  }
  dist_zip=$(ls dist/AngusGM-*.zip)
  scp -rp $dist_zip "${host}:/tmp/" || {
    echo "ERROR: Failed to copy app zip file"; exit 1
  }
  dist_file=$(basename "$dist_zip")
  ssh "$host" "unzip -qo /tmp/${dist_file} -d ${REMOTE_APP_DIR}" || {
    echo "ERROR: Failed to uzip to app directory"; exit 1
  }
  ssh "$host" "cp -f ${REMOTE_APP_CONF_DIR}/.priv-test.env ${REMOTE_APP_DIR}/conf/.priv.env" || {
    echo "ERROR: Failed to copy env files"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && sh startup-gm.sh debug" || {
    echo "ERROR: Failed to start app"; exit 1
  }
  sh builds/check-health.sh ${host} ${PRIVATE_PORT} || {
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
  env0=${env##*.};
  editionType0=$(echo "${editionType##*.}" | tr '[:lower:]' '[:upper:]')
  echo "INFO: npm build deployEnv=${env0} , editionType=${editionType0}"
  npm run build:env -- --env=${env0} --edition_type=${editionType0}
  if [ $? -ne 0 ]; then
    echo "ERROR: 'npm run build:env' failed, exiting script"
    exit 1
  fi
  echo "INFO: npm build end"
}

# Deploy web module
deploy_web() {
  echo "INFO: Deploying web module to ${host}"
  ssh "$host" "mkdir -p ${REMOTE_APP_STATIC_DIR} && rm -rf ${REMOTE_APP_STATIC_DIR}/*" || {
    echo "ERROR: Failed to clean static directory"; exit 1
  }
  scp -rp "${WEB_DIR}/dist"/* "${host}:${REMOTE_APP_STATIC_DIR}/" || {
    echo "ERROR: Failed to copy web assets"; exit 1
  }
  nginxFileName="${WEB_DIR}/nginx/nginx_${env##*.}_gm.conf"
  scp -p ${nginxFileName} "${host}:${NGINX_CONFIG_DIR}/" || {
    echo "ERROR: Failed to copy web assets"; exit 1
  }
  ssh "$host" "nginx -s reload" || {
    echo "ERROR: Failed to reload nginx"; exit 1
  }
}

ci_by_module(){
  # clone_repository
  if echo "$module" | grep -q "service"; then
    echo "INFO: Building service module"
    cd "$SERVICE_DIR" || exit 1
    maven_build || {
      echo "ERROR: Service build failed"; exit 1
    }
    cd ..
  fi

  if echo "$module" | grep -q "web"; then
    echo "INFO: Building web module"
    cd "$WEB_DIR" || exit 1
    npm_build || {
      echo "ERROR: Web build failed"; exit 1
    }
    cd ..
  fi
}

ci_private_edition(){
  # clone_repository
    echo "INFO: Building private app"

    echo "INFO: Building web module"
    cd web || exit 1
    npm_build || {
      echo "ERROR: Web build failed"; exit 1
    }
    cd ..

    echo "INFO: Building service module and packaging app"
    maven_build || {
      echo "ERROR: Private app build failed"; exit 1
    }
}

cd_by_module(){
  if [ -n "$hosts" ]; then
    echo "INFO: Starting deployment to hosts: ${hosts}"

    OLD_IFS="$IFS"
    IFS=','

    for host in $hosts; do
      if echo "$module" | grep -q "service"; then
        deploy_service
      fi
      if echo "$module" | grep -q "web"; then
        deploy_web
      fi
    done

    IFS="$OLD_IFS"
  else
    echo "INFO: No hosts specified, skipping deployment"
  fi
}

cd_private_edition(){
  if [ -n "$hosts" ]; then
    echo "INFO: Starting deployment to hosts: ${hosts}"
    OLD_IFS="$IFS"
    IFS=','

    for host in $hosts; do
      deploy_private_edition
    done

    IFS="$OLD_IFS"
  else
    echo "INFO: No hosts specified, skipping deployment"
  fi
}

cicd(){
  if echo "$editionType" | grep -q "edition.cloud_service"; then
    echo "INFO: Building cloud_service edition module"
    ci_by_module
    cd_by_module
  else
    echo "INFO: Building private edition app"
    ci_private_edition
    cd_private_edition
  fi
}

# Main execution flow
while [ $# -gt 0 ]; do
  case "$1" in
    --module) module="$2"; shift ;;
    --env) env="$2"; shift ;;
    --editionType) editionType="$2"; shift ;;
    --hosts) hosts="$2"; shift ;;
    --dbType) dbType="${2:-db.mysql}"; shift ;;
    #--gitBranch) gitBranch="$2"; shift ;;
    #--gitCredential) gitCredential="$2"; shift ;;
    *) echo "WARN: Unknown parameter $1"; shift ;;
  esac
  shift
done

# Step 1: Parameter validation
validate_parameters

# Step 2: Environment preparation
prepare_environment

# Step 3: CI and CD Phase
cicd

echo "SUCCESS: CI/CD pipeline completed successfully"
exit 0
