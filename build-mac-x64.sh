# arch has to be i386
# this can be configured by calling
#
# env /usr/bin/arch -x86_64 /bin/zsh --login
#
# on aarch64 macs. After that, make sure you are using a x64 java to compile and build the application.
#
# You can easily manage your java installation with `sdkman` like this:
#
# sdk use java 19.ea.19-open
#
mvn clean install -POS.osx.x64 -T1C; open ./dist/dist-osx/installer-osx/target/installer/LogoRRR-23.1.1-x86_64.pkg
