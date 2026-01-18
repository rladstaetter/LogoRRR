#!/bin/bash

set -e  # Exit on error, except where overridden

source "$(dirname "$0")/scripts/common.sh"

echo "cleaning deb"
sudo apt purge -y logorrr || true

echo "uninstall flatpak"
if ! flatpak uninstall  --delete-data --user -y app.logorrr.LogoRRR 2>/dev/null; then
  echo "Warning: Flatpak app 'app.logorrr.LogoRRR' not found or already uninstalled."
fi

# build everything on linux
build package app.logorrr.dist.linux:app-image,app.logorrr.dist.linux:deb,app.logorrr.dist.linux.flatpak:flatpak-package,app.logorrr.dist.linux:graal-linux

# Install the appropriate .deb
DEB_PATH="./dist/linux/deb/target/installer/logorrr_${PROJECTVERSION}_${DEB_ARCH}.deb"

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

echo "Running GraalVM variant"
./dist/linux/graal-linux/target/native/logorrr

echo "Congrats, you've got all variants of logorrr running"


