#!/bin/bash
# common.sh - Shared configuration and platform detection

set -e  # Exit on error, except where overridden

export PROJECTVERSION="26.1.0"

# Detect platform
ARCH=$(uname -m)

case "$ARCH" in
  x86_64)
    DEB_ARCH="amd64"
    ;;
  aarch64 | arm64)
    DEB_ARCH="arm64"
    ;;
  *)
    echo "Unsupported architecture: $ARCH"
    exit 1
    ;;
esac

export DEB_ARCH


build() {
    local TARGET=$1

    # Check if an argument was provided
    if [[ -z "$TARGET" ]]; then
        echo "Error: No target provided"
        return 1
    fi

    echo "----------------------------------------------------"
    echo "Building Target: $TARGET"
    echo "----------------------------------------------------"

    # Execute Maven
    # Use -B (Batch Mode) for cleaner script logs
    time ./mvnw clean package -pl "$TARGET" -am -B
}