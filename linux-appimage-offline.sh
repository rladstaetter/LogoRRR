#!/bin/bash

echo "cleaning"
rm -rf ./temp-repo

mvn process-resources -pl "app.logorrr:env"

echo "building"
mvn clean package -pl "app.logorrr.dist.linux:app-image" -am -B -Dmaven.repo.local=./temp-repo -Denforcer.skip=true
