# Software used for this project

## Implementation language

`LogoRRR` is implemented in [Scala](https://www.scala-lang.org), a programming language which allows you to combine functional and object oriented programming. It compiles down to the Java Virtual Machine (JVM).

## GUI Library

As User Interface Technology `LogoRRR` uses [JavaFX](https://openjfx.io). With the help of JavaFX it is possible to create Desktop Applications with Java or JVM based languages.

## Build Tool

`LogoRRR` uses [Apache Maven](https://maven.apache.org) as it's build tool. Maven is a well established build tool for the Java ecosystem, and Gluon provided a JavaFX plugin for building JavaFX applications for it.

## Installer

### Windows

LogoRRR is available in the [Windows App Store](https://aka.ms/AAr3sxs). 

Installers for Windows are also available. [Advanced Installer](https://www.advancedinstaller.com) was used to create the windows installer. LogoRRR was donated by the Advanced Installer team with an [Opensource License](https://www.advancedinstaller.com/free-license.html) - Thanks for sponsoring this project! 


### MacOs

On MacOsX, LogoRRR is available in the [Mac App Store](https://apps.apple.com/at/app/logorrr/id1583786769).

### Linux

The easiest way to install LogoRRR on Linux is to use the [snapcraft](https://www.snapcraft.io/logorrr) or [flatpak](https://flathub.org/en/apps/app.logorrr.LogoRRR) builds. 

Alternatively, you can install `LogoRRR` via the provided `.deb` files via 

    sudo apt install <release>.deb

## Development information

LogoRRR needs a JDK and Maven to be installed on your machine. The easiest way to build it is to use the provided shell scripts / bat files. 