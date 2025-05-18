#!/bin/sh

# ---------------------------------------------------------------------------
# OpenJDK 17+ Initialization Script. Auto-installs JDK 17 if not present/outdated.
# Usage: ./init-jdk.sh
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
JDK_DIR="${SCRIPT_DIR}/jdk"
TEMP_DIR=$(mktemp -d)

# OpenJDK 17 Download URLs
JDK_URL_LINUX="https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz"
JDK_URL_MAC="https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_macos-x64_bin.tar.gz"

# Function from previous implementation
check_jdk_version() {
    if [ -d "$JDK_DIR" ]; then
       export JAVA_HOME="$JDK_DIR"
       echo "The JAVA_HOME has been set to: $JAVA_HOME"
       return 0
    fi

    . /etc/profile

    if ! command -v java >/dev/null 2>&1; then
        return 1  # Java not found
    fi

    java_version_output=$(java -version 2>&1)
    major_version=$(echo "$java_version_output" | awk -F '"' '/version/ {print $2}' |
        awk -F '.' '{
            if ($1 == "1") { print $2 }
            else { print $1 }
        }')

    if ! echo "$major_version" | grep -qE '^[0-9]+$'; then
        return 2  # Version detection failed
    fi

    [ "$major_version" -ge 17 ]
}

# Install JDK 17
install_jdk() {
    # Detect OS and architecture
    case "$(uname -s)" in
        Linux*)     OS=linux;;
        Darwin*)    OS=mac;;
        *)          echo "ERROR: Unsupported OS"; exit 1;;
    esac

    # Select appropriate JDK package
    case "$OS" in
        linux) JDK_URL="$JDK_URL_LINUX";;
        mac)   JDK_URL="$JDK_URL_MAC";;
    esac

    echo "INFO: Downloading OpenJDK 17..."
    if ! curl -sL "$JDK_URL" -o "${TEMP_DIR}/jdk.tar.gz"; then
        echo "ERROR: Failed to download JDK" >&2
        rm -rf "$TEMP_DIR"
        exit 1
    fi

    echo "INFO: Extracting JDK..."
    mkdir -p "$JDK_DIR"
    tar xzf "${TEMP_DIR}/jdk.tar.gz" -C "$JDK_DIR" --strip-components=1

    # Cleanup
    rm -rf "$TEMP_DIR"
}

# Main execution
if ! check_jdk_version; then
    echo "WARN: Valid JDK 17+ not found, installing..."
    install_jdk
fi

# Set JAVA_HOME
if [ -d "${JDK_DIR}" ]; then
    export JAVA_HOME="${JDK_DIR}"
    echo "INFO: Using local JDK at ${JAVA_HOME}"
elif command -v java >/dev/null 2>&1; then
    export JAVA_HOME=$(java -XshowSettings:properties -version 2>&1 |
        awk -F '= ' '/java.home/ {print $2}' |
        head -n1)
    echo "INFO: Using system JDK at ${JAVA_HOME}"
else
    echo "ERROR: JDK installation failed" >&2
    exit 1
fi

# Verification
echo "Java Home: ${JAVA_HOME}"
echo "Java Version:" && "${JAVA_HOME}/bin/java" -version
