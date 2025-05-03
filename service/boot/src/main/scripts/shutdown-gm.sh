#!/bin/sh

# ---------------------------------------------------------------------------
# Stop script for the AngusGM application (Initialize by Maven).
# Usage: ./shutdown-gm.sh
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

SLEEP=6
EXECUTABLE=@project.build.finalName@.jar

CURRENT_HOME=`dirname "$0"`
# Only set GM_HOME if not already set
[ -z "$GM_HOME" ] && GM_HOME=`cd "$CURRENT_HOME" >/dev/null; pwd`
echo "Home Dir: $GM_HOME"

GM_PID="${GM_HOME}/gm.pid"
if [ -f "$GM_PID" ]; then
    if [ -s "$GM_PID" ]; then
        kill -0 `cat "$GM_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
            echo "PID file found but no matching process was found."
        else
            PID=`cat "$GM_PID"` # GM process exists
            rm -f "$GM_PID" >/dev/null 2>&1
        fi
    else
        echo "PID file is empty"
        rm -f "$GM_PID" >/dev/null 2>&1
    fi
else
    echo "PID file not found"
fi

if [ -z "$PID" ]; then
    PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
    if [ -z "$PID" ]; then
        echo "AngusGM process not found, shutdown aborted."
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
    echo "AngusGM process is killed, PID=$PID"
else
    echo "AngusGM process is stopped"
fi;
