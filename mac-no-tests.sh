#!/bin/bash

mvn clean -T1C
MAVEN_OPTS="--enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow" mvn install -T1C -Dmaven.test.skip=true
open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-25.2.0.pkg
