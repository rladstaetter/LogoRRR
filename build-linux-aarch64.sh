#!/bin/bash

mvn clean install -T1C
sudo apt install ./dist/dist-linux/installer-linux/target/installer/logorrr_24.5.0_arm64.deb
/opt/logorrr/bin/LogoRRR