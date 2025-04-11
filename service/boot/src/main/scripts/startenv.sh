#!/bin/sh
IP=$1
export JAVA_OPTS="-Xnoagent -Xlog:gc:logs/gc.log"
export JAVA_OPTS="$JAVA_OPTS -server -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -Djava.SECURITY.egd=file:/dev/./urandom"
export JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${IP}:2806"