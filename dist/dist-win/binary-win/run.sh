#!/bin/bash
mvn clean package
cp /Users/lad/gh/LogoRRR/native/native-osx/target/libLogoRRR.dylib target/gluonfx/aarch64-darwin/
cp /Users/lad/gh/LogoRRR/native/native-osx/target/libLogoRRRSwift.dylib target/gluonfx/aarch64-darwin/
cd target/gluonfx/aarch64-darwin/
./app.logorrr.dist.win.binary
