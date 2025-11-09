#!/bin/sh

# ---------------------------------------------------------------------------
# Start script for the angus application (Initialize by Maven).
# Usage: ./startup-@archive.name@.sh [--daemon] [--debug]
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

# Init java environment
initJdk(){
  if [ -z "${JAVA_HOME}" ]; then
    . ./init-jdk.sh
  fi

  if [ -z "${JAVA_HOME}" ]; then
    echo "JAVA_HOME is not set"
    exit 2
  fi
}

# Check that target jar exists
checkAppJar(){
  EXECUTABLE=@project.build.finalName@.jar
  if [ ! -f "$APP_HOME"/"$EXECUTABLE" ]; then
      echo "Cannot find $APP_HOME/$EXECUTABLE"
      echo "The jar file is absent"
      exit 1
  else
      chmod +x "$APP_HOME"/"$EXECUTABLE"
  fi
}

# Init application dir
initAppDir(){
  # Define config path
  if [ -z "$APP_CONF_DIR" ] ; then
      APP_CONF_DIR="$APP_HOME"/conf
      APP_CONF_LOG_FILE="$APP_CONF_DIR"/@archive.name@-logback.xml

      # If the configuration directory is empty, copy from the default configuration (In docker)
      if [ -z "$(ls -A ${APP_CONF_DIR})" ]; then
          echo "Initialize the configuration files by copy default-conf ..."
          cp -rp ${APP_HOME}/default-conf/* ${APP_HOME}/conf
          cp -rp ${APP_HOME}/default-conf/.priv* ${APP_HOME}/conf
      fi
  fi
  echo "Conf Dir: $APP_CONF_DIR"

  # Define plugins path
  if [ -z "$PLUGIN_DIR" ] ; then
      PLUGIN_DIR="$APP_HOME"/plugins

      # If the plugins directory is empty, copy from the default plugins (In docker)
      if [ -z "$(ls -A ${PLUGIN_DIR})" ]; then
          echo "Initialize the plugin files by copy default-plugins ..."
          cp -rp ${APP_HOME}/default-plugins/* ${APP_HOME}/plugins
      fi
  fi
  echo "Plugin Dir: $PLUGIN_DIR"

  # Define the console log path for the application
  if [ -z "$APP_LOG_DIR" ] ; then
      APP_LOG_DIR="$APP_HOME"/logs
      APP_CONSOLE_LOG="$APP_LOG_DIR"/@archive.name@-console.log
      # Create logs path
      mkdir -p "$APP_LOG_DIR" && touch "$APP_CONSOLE_LOG"
  fi
  echo "Logs Dir: $APP_LOG_DIR"

  # Define the java.io.tmpdir to use for application
  if [ -z "$APP_TMPDIR" ] ; then
      APP_TMPDIR="$APP_HOME"/tmp
      # Create temp path
      mkdir -p "$APP_TMPDIR"
  fi
  echo "Temp Dir: $APP_TMPDIR"
}

# Check the process exists
checkRunning(){
    PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
    if [ ! -z "${PID}" ]; then
        echo $PID > "$APP_PID"
        echo "@hump.name@ appears to still be running with PID $PID. Startup aborted."
        echo "If the following process is not a application process, remove the PID file and try again:"
        ps -f -p $PID
        return 0
    else
        return 1
    fi;
}

# Find application process PID
checkProcess(){
  APP_PID="${APP_HOME}/@archive.name@.pid"
  if [ -e "$APP_PID" ]; then
      if [ -s "$APP_PID" ]; then
          echo "Existing PID file found during application startup."
          if [ -r "$APP_PID" ]; then
              PID=`cat "$APP_PID"`
              ps -p $PID >/dev/null 2>&1
              if [ $? -eq 0 ] ; then
                  echo "@hump.name@ appears to still be running with PID $PID. Startup aborted."
                  echo "If the following process is not a application process, remove the PID file and try again:"
                  ps -f -p $PID
                  exit 1
              else
                  echo "@hump.name@ process does not exist and removing stale PID file."
                  checkRunning
                  if [ $? -eq "0" ]; then
                      echo $PID > "$APP_PID"
                      exit 1
                  else
                      rm -f $APP_PID >/dev/null 2>&1
                      if [ $? != 0 ]; then
                          if [ -w "$APP_PID" ]; then
                              cat /dev/null > $APP_PID
                          else
                              echo "Unable to remove stale PID file. Startup aborted."
                              exit 1
                          fi
                      fi
                  fi
              fi
          else
              echo "Unable to read PID file. Startup aborted."
              exit 1
          fi
      else # pid is empty
          checkRunning
          if [ $? -eq "0" ]; then
              echo $PID > "$APP_PID"
              exit 1
          else
              rm -f "$APP_PID" >/dev/null 2>&1
              if [ ! -w "$APP_PID" ]; then
                  echo "Unable to delete empty PID file. Startup aborted."
                  exit 1
              fi
          fi
      fi
  else
      checkRunning
      if [ $? -eq "0" ]; then
          echo "@hump.name@ PID file($APP_PID) is missing. Update PID"
          echo $PID > "$APP_PID" # touch $APP_PID
          exit 1
      fi
  fi
}

# Function to retrieve the primary IPv4 address (compatible with major Linux distributions and macOS)
get_local_ip() {
  # Attempt to get IP using modern 'ip' command
  ip_address=$(ip -4 addr show scope global 2>/dev/null |
               awk '/inet / {split($2, arr, "/"); print arr[1]; exit}')

  # Fallback to ifconfig if 'ip' command fails (macOS compatibility)
  [ -z "$ip_address" ] && ip_address=$(ifconfig 2>/dev/null |
               awk '/inet / && !/127.0.0.1/ {split($2, arr, " "); print arr[1]; exit}')

  echo "$ip_address"
}

# Function to generate a random port in safe range (20480-65535)
generate_random_port() {
  min_port=20480
  max_port=65535
  port_range=$((max_port - min_port + 1))

  # Try different POSIX-compliant methods
  if command -v shuf >/dev/null 2>&1; then
    shuf -i "${min_port}-${max_port}" -n 1
  elif [ -c /dev/urandom ]; then
    # Use od to read bytes from /dev/urandom
    raw=$(od -An -N2 -t u2 /dev/urandom 2>/dev/null | tr -d ' ')
    [ -n "$raw" ] && echo $(( (raw % port_range) + min_port ))
  else
    # Fallback to nanoseconds (less secure)
    ns=$(date +%N | sed 's/^0*//')
    echo $(( (ns % port_range) + min_port ))
  fi | awk 'NF {print $1}'
}

# Run application
runApp(){
  JAVA_OPTS="${JAVA_OPTS} -server -Djava.security.egd=file:/dev/./urandom -Dio.netty.tryReflectionSetAccessible=true"
  if [ "$DEBUG_MODE" = "true" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(get_local_ip):$(generate_random_port)"
  fi

  if [ "$DAEMON_MODE" = "true" ]; then
      nohup ${JAVA_HOME}/bin/java -Xms256m -Xmx1600m -jar $JAVA_OPTS \
        -DHOME_DIR=$APP_HOME \
        -DCONF_DIR=$APP_CONF_DIR \
        -DLOGS_DIR=$APP_LOG_DIR \
        -DPLUGIN_DIR=$PLUGIN_DIR \
        -Dlogback.configurationFile=$APP_CONF_LOG_FILE \
        -Djava.io.tmpdir=$APP_TMPDIR \
       $APP_HOME/$EXECUTABLE >> "$APP_CONSOLE_LOG" 2>&1 &
      echo $! > "$APP_PID"

      echo "@hump.name@ started, PID=$!"
  else
     ${JAVA_HOME}/bin/java -jar $JAVA_OPTS \
      -DHOME_DIR=$APP_HOME \
      -DCONF_DIR=$APP_CONF_DIR \
      -DLOGS_DIR=$APP_LOG_DIR \
      -DPLUGIN_DIR=$PLUGIN_DIR \
      -Dlogback.configurationFile=$APP_CONF_LOG_FILE \
      -Djava.io.tmpdir=$APP_TMPDIR \
     $APP_HOME/$EXECUTABLE
  fi

}

### Main Flow ###

CURRENT_HOME=`dirname "$0"`
# Only set APP_HOME if not already set
[ -z "$APP_HOME" ] && APP_HOME=`cd "$CURRENT_HOME" >/dev/null; pwd`
echo "App Home: $APP_HOME"

DAEMON_MODE=true
DEBUG_MODE=false
while [ $# -gt 0 ]; do
    case "$1" in
        --daemon=*)
            DAEMON_MODE="${1#*=}"
            ;;
        --daemon)
            DAEMON_MODE=true
            ;;
        --debug=*)
            DEBUG_MODE="${1#*=}"
            ;;
        --debug)
            DEBUG_MODE=true
            ;;
        *)
            echo "Warning: Ignore parameter '$1'"
            ;;
    esac
    shift
done

initJdk
checkAppJar
initAppDir
checkProcess
runApp
