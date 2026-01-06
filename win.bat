@echo off
ECHO Cleaning LogoRRR
cmd /C mvnw.cmd clean
ECHO Building LogoRRR
cmd /C mvnw.cmd clean install
ECHO Installing LogoRRR
cmd /C .\dist\dist-win\installer-win-jre\target\installer\LogoRRR-26.1.0-installer.exe