#!/bin/bash
# common.sh - Shared configuration and platform detection

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
