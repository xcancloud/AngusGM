#!/bin/bash

# ---------------------------------------------------------------------------
# AngusGM Service Health Check Script (Not applicable to privatized edition).
# Usage: sh check-health.sh <host>
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

# Check parameters
if [ $# -ne 1 ]; then
    echo "Usage: $0 <host>"
    exit 1
fi

# Configuration parameters
HOST="$1"
HEALTH_URL="http://$HOST:1806/actuator/health"
TIMEOUT=90          # Total timeout in seconds
INTERVAL=3          # Check interval in seconds

# Timeout control
start_time=$(date +%s)
timeout_end=$((start_time + TIMEOUT))

echo "Checking service health (Timeout: ${TIMEOUT}s)..."

# Health check loop
while [[ $(date +%s) -lt $timeout_end ]]; do
    # Get HTTP status code and response body in one call
    response=$(curl -s -w "\n%{http_code}" "$HEALTH_URL")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    # Dual validation logic
    if [ "$http_code" -eq 200 ]; then
        if echo "$body" | grep -q '"status":"UP"'; then
            echo "✅ Service is UP!"
            exit 0
        fi
    fi

    sleep $INTERVAL
done

# Timeout handling
echo "Timeout: Service not ready within ${TIMEOUT}s"
exit 1
