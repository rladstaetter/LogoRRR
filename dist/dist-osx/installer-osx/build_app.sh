#!/bin/bash

# ------ ENVIRONMENT --------------------------------------------------------
# The script depends on various environment variables to exist in order to
# run properly. The java version we want to use, the location of the java
# binaries (java home), and the project version as defined inside the pom.xml
# file, e.g. 1.0-SNAPSHOT.
#
# PROJECT_VERSION: version used in pom.xml, e.g. 1.0-SNAPSHOT
# APP_VERSION: the application version, e.g. 1.0.0, shown in "about" dialog
PROJECT_VERSION=21.3.1-SNAPSHOT
APP_VERSION=23.3.1

# Set desired installer type: "app-image", "dmg", "pkg", "rpm" or "deb".
INSTALLER_TYPE=app-image

echo "java home: $JAVA_HOME"
echo "project version: $PROJECT_VERSION"
echo "app version: $APP_VERSION"

# ------ SETUP DIRECTORIES AND FILES ----------------------------------------
# Remove previously generated java runtime and installers. Copy all required
# jar files into the input/libs folder.

rm -rfd ./target/java-runtime/
rm -rfd target/installer/

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --strip-native-commands \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules "jdk.crypto.ec,jdk.localedata,java.base,java.desktop,jdk.jfr,jdk.unsupported" \
  --include-locales=en \
  --output target/java-runtime

# ------ PACKAGING ----------------------------------------------------------
# In the end we will find the package inside the target/installer directory.

echo "Creating installer of type $INSTALLER_TYPE"

$JAVA_HOME/bin/jpackage \
--type $INSTALLER_TYPE \
--runtime-image target/java-runtime \
--input target/libs \
--main-class app.logorrr.LogoRRRAppLauncher \
--main-jar app-$PROJECT_VERSION.jar \
--name LogoRRR \
--mac-package-name LogoRRR \
--mac-package-identifier app.logorrr \
--app-version ${APP_VERSION} \
--icon src/main/resources/logorrr-icon-256.icns \
--vendor "app.logorrr" \
--copyright "Copyright © 2021 Logorrr.app" \
--java-options -Xmx4096m \
--dest target/installer \
