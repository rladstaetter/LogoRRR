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

# delete flatpak installation
flatpak uninstall --user app.logorrr.LogoRRR

# Maven build
MAVEN_OPTS="--enable-native-access=ALL-UNNAMED" mvn clean install -T1C

# Install the appropriate .deb
DEB_PATH="./dist/dist-linux/deb/target/installer/logorrr_25.1.0_${DEB_ARCH}.deb"
sudo apt install "$DEB_PATH"

# Run the app (installed via .deb)
/opt/logorrr/bin/LogoRRR

# run the app (installed via flatpak)
flatpak run app.logorrr.LogoRRR
