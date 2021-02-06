# javafx logboard

![Screenshot](screenshot.png)

JavaFX logboard is a simple utility to display a logfile and visualise certain classes of events. 

For example, ERROR events are visualized as red rectangles, TRACE events as grey rectangles, INFO events as green ones etc. 

The idea is to start this application and simply drag'n drop a logfile to it.

## Motivation

This project serves as a vehicle to test and learn about various aspects of JavaFX GraalVM compilation.

## Prerequisites

You have to download a recent GraalVM installation package (I've tested it with 21.0.0.r11) and a maven distribution
(everything starting from 3.6.3 should work).

## Building

Given you have followed the instructions for [maven-client-plugin](https://github.com/gluonhq/client-maven-plugin) you only have to provide the path to your GraalVM installation in the main pom.xml found at the root folder of the project.


### MacOs X

     mvn clean install

Should create a binary executable which can be run without a JVM:

    ./binary/target/client/x86_64-darwin/net.ladstatt.logboard.binary

### Windows    

You have to enter following command in a Visual Studio Developer console:

     mvn clean install

If everything goes as planned, a binary is available here after some minutes:
    
    binary\target\client\x86_64-windows\net.ladstatt.logboard.binary.exe

If you want to execute this exe on another computer you have to make sure that the Visual Studio runtime environment
is present there (redistributables).

Tip: A great tool which can be used to reduce file size for executables is [UPX](https://upx.github.io).

## Running from IntelliJ

Running JavaFX applications in IntelliJ needs a little bit more work if you work with modularized Java. There are builds
of Java which incorporate JavaFX (from Azul for example) where following steps are not necessary. However, if you use GraalVM or AdoptOpenJDK builds, you have to provide special VM Parameters in order to start a JavaFX application
from your IDE. 

As such, you have to provide following parameters:

    --module-path <path to your javafx sdk lib directory> --add-modules javafx.controls,javafx.fxml

For me following settings work:

    --module-path /Users/lad/gh/javafx-sdk-16/lib --add-modules javafx.controls,javafx.fxml

Note that you have to download this javafx-sdk separately and install it once on your computer, you can get it on 
[Gluon's Download Page for JavaFX](https://gluonhq.com/products/javafx/). 

A comprehensive tutorial how to get started with developing JavaFX can be found on [openjfx.io](https://openjfx.io). 

## License

This software is licensed under Apache-2 License.

## Further work

There are many ways to improve this application, it was primarily written to learn about GraalVM and JavaFX. For more details, check out [this blog post](https://ladstatt.blogspot.com/2020/10/compile-scala-javafx-application-with.html) which elaborates on some details of this project.
