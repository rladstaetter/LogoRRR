#!/bin/bash

MAVEN_OPTS="--enable-native-access=ALL-UNNAMED" mvn clean install -T1C -Papp-tests
open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-25.1.0.pkg
