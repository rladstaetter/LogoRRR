#!/bin/bash

set -e  # Exit on error, except where overridden

# Load shared variables
source "$(dirname "$0")/scripts/common.sh"

mvn clean package -pl app.logorrr.dist.linux:app-image -am
