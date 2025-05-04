#!/bin/bash

mvn compile

export MAVEN_OPTS="-Djava.library.path=/Users/lad/gh/LogoRRR/env/target/javafx-sdk-24.0.1/lib:/Users/lad/gh/LogoRRR/native/native-osx/target"

for i in {0..3}; do
  echo "Launching with argument: $i"
  mvn exec:java -Dexec.args="$i"
done