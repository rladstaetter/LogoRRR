#!/bin/bash

source "$(dirname "$0")/scripts/common.sh"
build app.logorrr:benchmarks

java --sun-misc-unsafe-memory-access=allow -jar benchmarks/target/benchmarks.jar -rf json -rff benchmarks/target/results.json

echo "drag'n drop results.json in website"

open benchmarks/target

open https://jmh.morethan.io


