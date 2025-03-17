#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$(dirname "$(realpath "$0")")"

# Liquibase container configuration
LIQUIBASE_IMAGE="liquibase/liquibase:latest-alpine"
NETWORK_NAME="ontrack_network"
CHANGELOG_DIR="$SCRIPT_DIR/migrations"
CONFIG_FILE="$SCRIPT_DIR/liquibase.docker.properties"

# Function to run Liquibase with given arguments
run_liquibase() {
  docker run --rm \
    --network="$NETWORK_NAME" \
    -v "$CHANGELOG_DIR:/liquibase/changelog" \
    -v "$CONFIG_FILE:/liquibase/liquibase.docker.properties" \
    "$LIQUIBASE_IMAGE" \
    --defaultsFile=liquibase.docker.properties "$@"
}

# Display usage/help text
show_help() {
  echo "Usage: $0 {update|rollback|help}"
  echo "Commands:"
  echo "  update       Apply new database changes"
  echo "  rollback     Revert the last change (rollback-count=1)"
  echo "  help         Show this help message"
  exit 0
}

# Ensure an argument is provided
[[ $# -eq 0 || "$1" == "help" ]] && show_help

# Handle commands
case "$1" in
  update)   run_liquibase update ;;
  rollback) run_liquibase rollback-count --count=1 ;;
  *)        show_help ;;  # Catch-all for invalid commands
esac
