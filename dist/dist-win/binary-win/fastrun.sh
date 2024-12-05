#!/bin/bash
mvn package
cd target/gluonfx/aarch64-darwin/
./app.logorrr.dist.win.binary
