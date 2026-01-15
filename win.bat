@echo off
ECHO Building a clean LogoRRR Windows Installer and zip distribution
cmd /C mvnw.cmd clean install -pl app.logorrr.dist.win:installer,app.logorrr.dist.win:app-image -am -B

ECHO Execute LogoRRR installer (Run as Adminstrator ...)
explorer .\dist\dist-win\installer-win-jre\target\installer\