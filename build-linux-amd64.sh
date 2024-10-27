#!/bin/bash

mvn clean install -T1C
sudo apt install ./dist/dist-linux/installer-linux/target/installer/logorrr_25.1.0_amd64.deb
/opt/logorrr/bin/LogoRRR
