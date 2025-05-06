#!/bin/sh

# ---------------------------------------------------------------------------
# Start script for the AngusGM application (Initialize by Maven).
# Usage: ./startup-gm.sh
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

CURRENT_HOME=`dirname "$0"`
# Only set GM_HOME if not already set
[ -z "$GM_HOME" ] && GM_HOME=`cd "$CURRENT_HOME" >/dev/null; pwd`
echo "App Home: $GM_HOME"

# Init java environment
. ./init-jdk.sh
if [ -z "${JAVA_HOME}" ]; then
  echo "JAVA_HOME is not set"
  exit 2
fi

# Check that target jar exists
EXECUTABLE=@project.build.finalName@.jar
if [ ! -f "$GM_HOME"/"$EXECUTABLE" ]; then
    echo "Cannot find $GM_HOME/$EXECUTABLE"
    echo "The jar file is absent"
    exit 1
else
    chmod +x "$GM_HOME"/"$EXECUTABLE"
fi

# Define config path
if [ -z "$GM_CONF_DIR" ] ; then
    GM_CONF_DIR="$GM_HOME"/conf
    GM_CONF_LOG_FILE="$GM_CONF_DIR"/@archive.name@-logback.xml
    # Create config path
    # mkdir -p "$GM_CONF_DIR"
fi
echo "Conf Dir: $GM_CONF_DIR"

# Define the console log path for the gm
if [ -z "$GM_LOG_DIR" ] ; then
    GM_LOG_DIR="$GM_HOME"/logs
    GM_CONSOLE_LOG="$GM_LOG_DIR"/@archive.name@-console.log
    # Create logs path
    mkdir -p "$GM_LOG_DIR" && touch "$GM_CONSOLE_LOG"
fi
echo "Logs Dir: $GM_LOG_DIR"

# Define the java.io.tmpdir to use for gm
if [ -z "$GM_TMPDIR" ] ; then
    GM_TMPDIR="$GM_HOME"/tmp
    # Create temp path
    mkdir -p "$GM_TMPDIR"
fi
echo "Temp Dir: $GM_TMPDIR"

# Check the process exists
running_check(){
    PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
    if [ ! -z "${PID}" ]; then
        echo $PID > "$GM_PID"
        echo "AngusGM appears to still be running with PID $PID. Startup aborted."
        echo "If the following process is not a AngusGM process, remove the PID file and try again:"
        ps -f -p $PID
        return 0
    else
        return 1
    fi;
}

# Find gm process PID
GM_PID="${GM_HOME}/gm.pid"
if [ -e "$GM_PID" ]; then
    if [ -s "$GM_PID" ]; then
        echo "Existing PID file found during AngusGM startup."
        if [ -r "$GM_PID" ]; then
            PID=`cat "$GM_PID"`
            ps -p $PID >/dev/null 2>&1
            if [ $? -eq 0 ] ; then
                echo "AngusGM appears to still be running with PID $PID. Startup aborted."
                echo "If the following process is not a AngusGM process, remove the PID file and try again:"
                ps -f -p $PID
                exit 1
            else
                echo "AngusGM process does not exist and removing stale PID file."
                running_check
                if [ $? -eq "0" ]; then
                    echo $PID > "$GM_PID"
                    exit 1
                else
                    rm -f $GM_PID >/dev/null 2>&1
                    if [ $? != 0 ]; then
                        if [ -w "$GM_PID" ]; then
                            cat /dev/null > $GM_PID
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
    else # gm.pid is empty
        running_check
        if [ $? -eq "0" ]; then
            echo $PID > "$GM_PID"
            exit 1
        else
            rm -f "$GM_PID" >/dev/null 2>&1
            if [ ! -w "$GM_PID" ]; then
                echo "Unable to delete empty PID file. Startup aborted."
                exit 1
            fi
        fi
    fi
else
    running_check
    if [ $? -eq "0" ]; then
        echo "AngusGM PID file($GM_PID) is missing. Update PID"
        echo $PID > "$GM_PID" # touch $GM_PID
        exit 1
    fi
fi

# Run gm
JAVA_OPTS="-server -Xnoagent -Djava.SECURITY.egd=file:/dev/./urandom -Dio.netty.tryReflectionSetAccessible=true"
nohup ${JAVA_HOME}/bin/java -jar $JAVA_OPTS \
  -DHOME_DIR=$GM_HOME \
  -DCONF_DIR=$GM_CONF_DIR \
  -DLOGS_DIR=$GM_LOG_DIR \
  -DPLUGIN_DIR=$GM_HOME/plugins \
  -Dlogback.configurationFile=$GM_CONF_LOG_FILE \
  -Djava.io.tmpdir=$GM_TMPDIR \
 $GM_HOME/$EXECUTABLE >> "$GM_CONSOLE_LOG" 2>&1 &
echo $! > "$GM_PID"

echo "AngusGM started, PID=$!"
