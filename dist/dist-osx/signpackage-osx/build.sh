#!/bin/sh
mvn clean compile assembly:single
cp target/SignPackage-1.0-jar-with-dependencies.jar ./SignPackage.jar

