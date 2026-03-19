#!/bin/bash

source "$(dirname "$0")/scripts/common.sh"

build clean app.logorrr:app-tests
build test app.logorrr:app-tests
