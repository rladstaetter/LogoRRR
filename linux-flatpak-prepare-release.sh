#!/bin/bash

source "$(dirname "$0")/scripts/common.sh"
build package app.logorrr.dist.linux.flatpak:flatpak-dist

echo "Copying final .yml to flatpak project ../LogoRRR-flatpak/app.logorrr.LogoRRR.yml"
cp dist/linux/flatpak/flatpak-dist/target/flatpak.archive/app.logorrr.LogoRRR.yml ../LogoRRR-flatpak/

echo "create pull request against flathub/app.logorrr.LogoRRR in LogoRRR-flatpak when ready (check ids and version number)"
