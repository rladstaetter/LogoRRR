#!/bin/bash

echo "cleaning"
rm -rf ./temp-repo

echo "building"
./mvnw clean package -pl "app.logorrr.dist.linux:app-image" -am -B -Dmaven.repo.local=./temp-repo