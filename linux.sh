#!/bin/bash

set -e  # Exit on error, except where overridden

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

# Attempt to uninstall Flatpak version, ignore error if not installed
if ! flatpak uninstall  --delete-data --user -y app.logorrr.LogoRRR 2>/dev/null; then
  echo "Warning: Flatpak app 'app.logorrr.LogoRRR' not found or already uninstalled."
fi

# Maven build
echo "Building project with Maven..."
MAVEN_OPTS="--enable-native-access=ALL-UNNAMED" mvn clean install -T1C

# Install the appropriate .deb
DEB_PATH="./dist/dist-linux/deb/target/installer/logorrr_25.1.1_${DEB_ARCH}.deb"

if [[ ! -f "$DEB_PATH" ]]; then
  echo "Error: .deb file not found at $DEB_PATH"
  exit 1
fi

echo "Installing .deb package..."
sudo apt install "$DEB_PATH"

# Run the app (installed via .deb)
/opt/logorrr/bin/LogoRRR

echo "Starting LogoRRR in flatpak container ... "

# run the app (installed via flatpak)
flatpak run app.logorrr.LogoRRR

# rebuild graalvm build (doesn't work in whole package)
echo "Rebuilding LogoRRR as GraalVM binary ... "
cd dist/dist-linux/graal-linux/
mvn package
./target/native/logorrr
cd ../../..


