# arch has to be i386
# this can be configured by calling
# env /usr/bin/arch -x86_64 /bin/zsh --login
# on aarch64 macs
mvn clean install -T1C; open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-22.3.0-x86_64.pkg
