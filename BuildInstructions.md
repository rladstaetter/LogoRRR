# Building LogoRRR from source code

## Prerequisites

There are several apps you will have to install to sucessfully build LogoRRR, but it isn't overly complicated.

Given you have followed the instructions for [maven-gluonfx-plugin](https://github.com/gluonhq/gluonfx-maven-plugin) you only have to provide the path to your GraalVM installation in the main pom.xml found at the root folder of the project.

### MacOs X

Following commands should get you a working environment. This is a fast way to install everything you'll need as far as I remember.

    git clone https://github.com/rladstaetter/LogoRRR.git
    curl -s "https://get.sdkman.io" | bash
    sdk install maven 3.8.1
    sdk install java 22.0.0.2.r11-grl

([sdkman.io](https://sdkman.io) is a package manager specifically tailored for java development, you can install various JVM based utilities / libraries and tools with it, manage different JVM versions as well - very handy!)

After this initial setup, you should be ready to build via maven. You have to adapt some paths in `pom.xml` - just search for `graalvm.home` and make sure it is pointing to the correct path.

Execute

     mvn clean install

in the project directory (where `pom.xml` is located).

After spending some time compiling all necessary modules a binary executable which can be run without a JVM should be
available here:

    ./dist/dist-osx/binary-osx/target/gluonfx/x86_64-darwin/app.logorrr.dist.osx.binary

### Windows

To setup your Windows development environment, you have to perform following steps:

- Install Visual Studio 2019 (Community Edition should suffice, tested with Visual Studio Professional)
- Gluon's build of GraalVM in a recent version (at least [22.0.0.2](https://github.com/gluonhq/graal/releases))
- (optionally) Install [Advanced Installer Suite](https://www.advancedinstaller.com) for creating installers (if you don't the build doesn't create installers)
- set `GRAALVM_HOME`, `JAVA_HOME` andy your PATH variable to use this JDK for your builds. 

For my setup, those commands work:

    set GRAALVM_HOME=C:\Program Files\Java\graalvm-svm-java11-windows-gluon-22.0.0.2-Final
    set JAVA_HOME=%GRAALVM_HOME%
    set PATH=%GRAALVM_HOME%\bin;%PATH%

After setting `graalvm.home` in the main pom.xml to your GraalVM Installation directory all you have to do to get to a binary is:

     mvn clean install

A binary is available here after some minutes:

    ./dist/dist-win/binary-win/target/gluonfx/x86_64-windows/app.logorrr.dist.win.binary.exe

If you want to execute this exe on another computer, you have to make sure that the Visual Studio runtime dlls are present there. If not, you can download [Visual Studio 2019 redistributables here](https://aka.ms/vs/16/release/vc_redist.x64.exe). If the application doesn't start - this is a possible reason for that.


## Running from IntelliJ

Running JavaFX applications in IntelliJ needs a some more work if you work with modularized Java. There are builds of Java which incorporate JavaFX (from Azul for example) where following steps are not necessary. However, if you use GraalVM or AdoptOpenJDK builds, you have to provide special VM Parameters in order to start a JavaFX application from your IDE.

As such, you have to provide following parameters:

    --module-path <path to your javafx sdk lib directory> --add-modules javafx.controls,javafx.fxml

For me following settings work:

    --module-path /Users/lad/gh/javafx-sdk-16/lib --add-modules javafx.controls,javafx.fxml

Note that you have to download this javafx-sdk separately and install it once on your computer, you can get it on [Gluon's Download Page for JavaFX](https://gluonhq.com/products/javafx/).

A comprehensive tutorial how to get started with developing JavaFX can be found on [openjfx.io](https://openjfx.io).

## Configuring GraalVM native-image

`gluonfx-maven-plugin` helps to configure GraalVM compilation by providing a command which creates a configuration for
`native-image` by analysing the runtime behavior of an application. Navigate to the `./dist/dist-win/binary-win/` or
`./dist/dist-osx/binary-osx/` subdirectory and enter following command:

    mvn gluonfx:runagent

It will start LogoRRR. Now you have to execute all possible click paths - in your code repository configuration files will appear in `src/main/resources/META-INF/native-image/`. Those files are different depending on which OS you execute the application.

Currently, those files are  checked in under src/main/native-image/<os>/, and via a maven configuration they are placed in the right place needed for native-image. Like this the project can be build on multiple platforms via

    mvn clean install 

Implicitly, maven package will call `gluonfx:build` and create a binary executable. 