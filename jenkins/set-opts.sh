#!/bin/sh

# ---------------------------------------------------------------------------
# Set the Java option parameters for starting AngusGM service (Not applicable to privatized edition).
# Usage: sh set-opts.sh <host>
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

HOST=$1
DEBUG_PORT=2806

export JAVA_OPTS="-Xnoagent -Xlog:gc:logs/gc.log"
export JAVA_OPTS="$JAVA_OPTS -server -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -Djava.SECURITY.egd=file:/dev/./urandom"
export JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${HOST}:${DEBUG_PORT}"
