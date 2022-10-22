# arch has to be i386
# this can be configured by calling
# env /usr/bin/arch -x86_64 /bin/zsh --login
# on aarch64 macs
mvn clean install -POS.osx.x64 -T1C; open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-23.1.0-x86_64.pkg
