@echo off
ECHO Building LogoRRR
cmd /C mvn clean package -q -T1C
ECHO Installing LogoRRR
cmd /C .\dist\dist-win\installer-win-jre\target\installer\LogoRRR-23.3.0-installer-jre.exe