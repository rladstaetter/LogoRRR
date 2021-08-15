# SignPackage

To notarize Java apps on macOS it is necessary to sign all jar and dylib files in the .app package with the same signature. 
dylib files can even exist inside jar files and have to be signed inside the jar file, too. This program here performs the
codesign process. You can use it e.g. like this:

```
java -jar SignPackage.jar -d appimageoutput -t -r -k "Developer ID Application: John Public (XXXXXXXXXX)" -e "src/main/deploy/package/macosx/MyApp.entitlements"
```

The parameters are:
```
-d The directory that contains the files that have to be codesigned.
-t Set secure timestamp using the codesign timestamp parameter
-r Harden using the codesign runtime parameter
-k Key name
-e Entitlements file
-x Excludes files from being signed. Can be used multiple times to specify multiple files. You have to specify the path including directories.
```

It is explained further here: https://blog.dgunia.de/2020/02/12/signed-macos-programs-with-java-14/

To build the SignPackage.jar file just run build.sh.
