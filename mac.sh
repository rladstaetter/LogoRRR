#!/bin/bash

source "$(dirname "$0")/scripts/common.sh"
build package app.logorrr.dist.osx:installer-osx

open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-26.2.0.pkg
