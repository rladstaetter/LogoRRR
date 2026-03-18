#!/bin/bash

echo "cleaning"
./mvnw clean -pl "app.logorrr.dist.linux:app-image"
rm -rf ./temp-repo

echo "fetching openjfx"
./mvnw generate-resources -pl "app.logorrr:env" -U

echo "building"
./mvnw clean package -pl "app.logorrr.dist.linux:app-image" -am -B -Dmaven.repo.local=./temp-repo