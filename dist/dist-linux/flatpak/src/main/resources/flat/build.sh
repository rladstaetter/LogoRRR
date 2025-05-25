#!/bin/bash

# Detect the architecture
ARCH=$(uname -m)

# Define source directories for architectures
X86_DIR="./files/x86_64"
AARCH64_DIR="./files/aarch64"

# Check architecture and copy corresponding files
if [ "$ARCH" == "x86_64" ]; then
    echo "Building for x86_64 architecture"
#    cp -r "$X86_DIR"/* "$FLATPAK_DEST/"
elif [ "$ARCH" == "aarch64" ]; then
    echo "Building for aarch64 architecture"
#    cp -r "$AARCH64_DIR"/* "$FLATPAK_DEST/"
else
    echo "Unsupported architecture: $ARCH"
    exit 1
fi
