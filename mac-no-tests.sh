#!/bin/bash

MAVEN_OPTS="--enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow" mvn clean install -T1C -Dmaven.test.skip=true
open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-25.1.1.pkg
