#!/bin/sh

# ---------------------------------------------------------------------------
# AngusGM Service Health Check Script (Not applicable to privatized edition).
# Usage: sh check-health.sh <host>
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

# Global variables
HOST="$1"
PORT=1806
ENDPOINT=""
MAX_ATTEMPTS=180
ATTEMPT=1
SLEEP=0.5

# Validate mandatory host parameter
validate_host() {
  if [ $# -eq 0 ]; then
    echo "ERROR: Host parameter is required. Usage: $0 <host>" >&2
    exit 1
  fi

  ENDPOINT="http://${HOST}:${PORT}/actuator/health"
}

# Check prerequisite commands
check_prerequisites() {
  # Verify curl installation
  if ! command -v curl >/dev/null 2>&1; then
    echo "ERROR: curl command not found. Please install curl first." >&2
    exit 1
  fi

  # Verify jq installation
  if ! command -v jq >/dev/null 2>&1; then
    echo "ERROR: jq command not found. Please install jq first." >&2
    exit 1
  fi
}

# Perform single health check
perform_health_check() {
  # Temporary files for processing
  tmpfile=$(mktemp /tmp/hcheck.txt)
  trap 'rm -f "$tmpfile"' EXIT

  # Get full response with status code
  curl_output=$(curl -s -w "\n%{http_code}" "${ENDPOINT}" 2>/dev/null >"$tmpfile")
  http_code=$(tail -n 1 "$tmpfile")
  json_body=$(sed '$ d' "$tmpfile")

  # Parse status value
  status=$(echo "$json_body" | jq -r '.status' 2>/dev/null || echo "UNKNOWN")

  # Output results
  echo "INFO: Check health ${http_code} ${status}"
}

# Main check loop
main() {
  echo "INFO: Starting health check for ${ENDPOINT}"

  while [ ${ATTEMPT} -le ${MAX_ATTEMPTS} ]; do
    echo "INFO: Attempt ${ATTEMPT}/${MAX_ATTEMPTS} - Checking service status..."

    # Execute health check
    result=$(perform_health_check)
    http_code=$(echo "$result" | cut -d ' ' -f 1)
    status=$(echo "$result" | cut -d ' ' -f 2)

    # Validate conditions
    if [ "${http_code}" = "200" ] && [ "${status}" = "UP" ]; then
      echo "SUCCESS: Service is healthy. HTTP Status: ${http_code}, Service Status: ${status}"
      exit 0
    else
      echo "WARN: Unhealthy status detected. HTTP Status: ${http_code:-N/A}, Service Status: ${status:-N/A}"
    fi

    # Wait for next attempt
    sleep ${SLEEP}
    ATTEMPT=$((ATTEMPT + 1))
  done

  echo "ERROR: Health check timed out after 90 seconds" >&2
  exit 1
}

# Execution flow
validate_host "$@"
check_prerequisites
main
