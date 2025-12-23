#!/bin/bash

set -e  # Exit on error, except where overridden

echo "cleaning all"
mvn clean -T1C

PROJECTVERSION="25.2.0"
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
DEB_PATH="./dist/dist-linux/deb/target/installer/logorrr_${PROJECTVERSION}_${DEB_ARCH}.deb"

if [[ ! -f "$DEB_PATH" ]]; then
  echo "Error: .deb file not found at $DEB_PATH"
  exit 1
fi

echo "Installing .deb package '$DEB_PATH'"
sudo apt install "$DEB_PATH"

# Run the app (installed via .deb)
/opt/logorrr/bin/LogoRRR

echo "Starting LogoRRR in flatpak container ... "

# run the app (installed via flatpak)
flatpak run app.logorrr.LogoRRR


# run graalvm compilation again, a workaround for the graalvm compiler behavior
# which places transitive dependencies into the output directory - but not if run
# as multimodule build (??)
echo "Rebuilding GraalVM variant"
cd dist/dist-linux/graal-linux/
mvn package
cd ../../..

echo "Running GraalVM variant"
./dist/dist-linux/graal-linux/target/native/logorrr


