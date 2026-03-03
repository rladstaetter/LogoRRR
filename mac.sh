#!/bin/bash

source "$(dirname "$0")/scripts/common.sh"
build package app.logorrr.dist.mac:installer-mac

open ./dist/mac/installer-mac/target/installer/LogoRRR-26.2.0.pkg
