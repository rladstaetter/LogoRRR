@echo off
ECHO Building a clean LogoRRR Windows Installer
cmd /C mvnw.cmd clean install -pl app.logorrr.dist:installer -am -B

ECHO Execute LogoRRR installer (Run as Adminstrator ...)
explorer .\dist\dist-win\installer-win-jre\target\installer\