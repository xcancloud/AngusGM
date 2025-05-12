#!/bin/sh

# ---------------------------------------------------------------------------
# Stop script for the @hump.name@ application (Initialize by Maven).
# Usage: ./shutdown-@archive.name@.sh
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

SLEEP=6
EXECUTABLE=@project.build.finalName@.jar

CURRENT_HOME=`dirname "$0"`
# Only set APP_HOME if not already set
[ -z "$APP_HOME" ] && APP_HOME=`cd "$CURRENT_HOME" >/dev/null; pwd`
echo "Home Dir: $APP_HOME"

APP_PID="${APP_HOME}/@archive.name@.pid"
if [ -f "$APP_PID" ]; then
    if [ -s "$APP_PID" ]; then
        kill -0 `cat "$APP_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
            echo "PID file found but no matching process was found."
        else
            PID=`cat "$APP_PID"` # Application process exists
            rm -f "$APP_PID" >/dev/null 2>&1
        fi
    else
        echo "PID file is empty"
        rm -f "$APP_PID" >/dev/null 2>&1
    fi
else
    echo "PID file not found"
fi

if [ -z "$PID" ]; then
    PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
    if [ -z "$PID" ]; then
        echo "@hump.name@ process not found, shutdown aborted."
        exit 0
    fi;
fi

echo "Attempting to stop the process through OS signal."
kill -15 $PID >/dev/null 2>&1
sleep 3

PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
if [ ! -z "$PID" ]; then
    sleep $SLEEP
    kill -9 $PID >/dev/null 2>&1
    echo "@hump.name@ process is killed, PID=$PID"
else
    echo "@hump.name@ process is stopped"
fi;
