@echo off
ECHO Building LogoRRR
cmd /C mvn clean package -q -T1C
ECHO Installing LogoRRR
cmd /C .\dist\dist-win\installer-win\target\installer\LogoRRR-24.4.0-installer.exe