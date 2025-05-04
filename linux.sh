#!/bin/bash

set -e  # Exit on error

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

# Maven build
mvn clean install -T1C

# Install the appropriate .deb
DEB_PATH="./dist/dist-linux/installer-linux/target/installer/logorrr_25.1.0_${DEB_ARCH}.deb"
sudo apt install "$DEB_PATH"

# Run the app
/opt/logorrr/bin/LogoRRR