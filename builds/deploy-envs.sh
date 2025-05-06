#!/bin/bash
# deploy-envs.sh - Secure environment files deployment tool
# Usage: ./deploy-envs.sh remote-host

# Get script absolute directory
SCRIPT_DIR=$(dirname "$(realpath "$0")")
PARENT_DIR=$(dirname "$SCRIPT_DIR")
ENV_SRC_DIR="$PARENT_DIR/service/conf"

# ANSI color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Parameter validation
if [ $# -eq 0 ]; then
    echo "${RED}Error: Remote server address must be specified${NC}"
    echo "Example: $0 <host_name>"
    exit 1
fi

REMOTE_HOST="$1"
TARGET_DIR="/data/apps/conf/gm"

# Validate environment configuration
validate_environment() {
    # Check if source directory exists
    if [ ! -d "$ENV_SRC_DIR" ]; then
        echo "${RED}Error: Environment directory not found $ENV_SRC_DIR${NC}"
        exit 2
    fi

    # Verify .env files existence
    if ! find "$ENV_SRC_DIR" -maxdepth 1 -name '.*.env' -print -quit | grep -q .; then
        echo "${YELLOW}Warning: No .env files found in directory: $ENV_SRC_DIR${NC}"
        exit 3
    fi
}

setup_remote_directory() {
    echo "${GREEN}Initializing remote directory: $TARGET_DIR${NC}"

    # Create directory with proper permissions
    if ! ssh "root@$REMOTE_HOST" "sudo mkdir -p '$TARGET_DIR' && sudo chmod 755 '$TARGET_DIR'"; then
        echo "${RED}✗ Failed to create remote directory${NC}"
        exit 4
    fi

    # Verify directory existence
    if ! ssh "root@$REMOTE_HOST" "test -d '$TARGET_DIR'"; then
        echo "${RED}✗ Directory validation failed${NC}"
        exit 5
    fi
}

# Deploy environment files
deploy_files() {
    echo "${GREEN}Deploying environment files to: $REMOTE_HOST${NC}"
    echo "Source directory: ${YELLOW}$ENV_SRC_DIR${NC}"
    echo "Target path: ${YELLOW}$TARGET_DIR${NC}"

    # Display file list to transfer
    echo "\n${GREEN}Files to transfer:${NC}"
    find "$ENV_SRC_DIR" -maxdepth 1 -name '.*.env' -print

    # Perform SCP transfer
    echo "\n${YELLOW}Starting transfer...${NC}"
    if scp -rpqC -o StrictHostKeyChecking=yes \
        "$ENV_SRC_DIR"/.*.env \
        "root@$REMOTE_HOST:$TARGET_DIR/"; then
        echo "\n${GREEN}✓ Environment files deployed successfully${NC}"
    else
        echo "\n${RED}✗ File transfer failed. Check error details${NC}"
        exit 4
    fi
}

# Main execution flow
main() {
    validate_environment
    setup_remote_directory
    deploy_files

    # Post-deployment verification (optional)
    echo "\n${YELLOW}Suggested verification command:${NC}"
    echo "ssh root@$REMOTE_HOST 'ls -la $TARGET_DIR/.*.env'"
}

# Execute main function
main
