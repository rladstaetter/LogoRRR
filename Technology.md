# Software used for this project

## Implementation language

LogoRRR is implemented in [Scala](https://www.scala-lang.org), a programming language which allows you to combine functional and object oriented programming. It compiles down to the Java Virtual Machine (JVM).

## GUI Library

As User Interface Technology LogoRRR uses [JavaFX](https://openjfx.io). With the help of JavaFX it is possible to create Desktop Applications with Java or JVM based languages.

## Build Tool

LogoRRR uses [Apache Maven](https://maven.apache.org) as it's build tool. Maven is a well established build tool for the Java ecosystem, and Gluon provided a JavaFX plugin for building JavaFX applications for it.

## Installer

### Windows

On windows, LogoRRR uses [GraalVM native image](https://www.graalvm.org/reference-manual/native-image/) to compile the application code to a native binary. To be able to use native image's features together with JavaFX, a company named [Gluon](https://gluonhq.com) has created a Maven plugin named [gluonfx-maven-plugin](https://github.com/gluonhq/gluonfx-maven-plugin) which makes it approachable to create AOT binaries based on JavaFX. In fact, the initial motivation for LogoRRR was to show that creating applications with JavaFX was feasible together with GraalVM. 

To enhance end user experience further, an installer framwework named [Advanced Installer](https://www.advancedinstaller.com) was used to create the windows installer. Advanced installer is in no way affiliated with either Gluon nor GraalVM, but can be regarded as its own tooling. It was choosen since the installer itself has a great UX and it is easy to use and is accessible for the project thanks to the employer of the main author ([Nextsense](https://www.nextsense-worldwide.com/en/)). 

To reduce the size of the application itself [UPX](https://upx.github.io) is used which really does a great job at minimizing the application size.

### MacOs

The MacOs Installer is built via [jpackage](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html) which is able to create a pkg and makes it easier to install LogoRRR for MacOsX.

## Misc

Some helper tools are being implemented to help create the releases or creating screenshots and the application icons, all implemented in Scala.
