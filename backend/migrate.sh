#!/bin/bash

# Display usage/help text
show_help() {
  echo "Usage: $0 {update|rollback|diff|help}"
  echo "Commands:"
  echo "  update       Apply new database changes"
  echo "  rollback     Revert the last change (rollback-count=1)"
  echo "  diff         Generate the DDL diff between the DB and the Hibernate Entities"
  echo "  help         Show this help message"
  exit 0
}

# Ensure an argument is provided
[[ $# -eq 0 || "$1" == "help" ]] && show_help

git_username=$(git config user.name)
echo "Running as Git user: $git_username"

# Handle commands
case "$1" in
  update)   docker compose exec backend ./gradlew update;;
  rollback)
  # Check if second argument exists and is a number, else fallback to 1
    if [[ -n "$2" && "$2" =~ ^[0-9]+$ ]]; then
      count="$2"
    else
      count=1
    fi
    docker compose exec backend ./gradlew rollbackCount -Dcount="$count";;
  diff)     docker compose exec backend ./gradlew diffChangelog -DrunList=diff -Dauthor="$git_username";;
  *)        show_help;;
esac
