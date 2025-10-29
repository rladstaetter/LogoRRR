@echo off
ECHO Building LogoRRR
cmd /C mvn clean install -T1C
ECHO Installing LogoRRR
cmd /C .\dist\dist-win\installer-win-jre\target\installer\LogoRRR-25.2.0-installer.exe