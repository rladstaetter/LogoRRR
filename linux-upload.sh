#!/bin/bash

set -e  # Exit on error, except where overridden

source "$(dirname "$0")/scripts/common.sh"

# build everything and upload linux binaries
build install app.logorrr.dist.linux:app-image,app.logorrr.dist.linux:deb,app.logorrr.dist.linux.flatpak:flatpak-package,app.logorrr.dist.linux:graal-linux

echo "Check artifacts on remote server https://www.logorrr.app/downloads/"
