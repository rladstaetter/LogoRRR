#!/bin/bash

mvn clean -T1C -Papp-tests
mvn clean install -Papp-tests
open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-26.1.0.pkg
