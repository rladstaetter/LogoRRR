#!/bin/bash

mvn clean install -T1C -Papp-tests
open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-24.5.0.pkg
