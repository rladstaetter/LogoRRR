# Security Bookmarks 

Since Mac App Store apps are sandboxed, file access is restricted and needs special consideration.

Of course LogoRRR needs access to files, and as such it poses a problem for the whole application. 

In order to grant LogoRRR access to files this module has been put into place.

Direct access from Java to the necessary swift code and Mac OsX APIs is only possible via calling it via C code.

Thus as an Java developer, you have to

- call native code via JNI
- from this intermediate layer, call the OsX API

This feels and is cumbersome, and yet another reason why it is not easy to create apps for Mac with the JVM. 

Of course, this includes additional complexities in building the code and deployment, more libraries which have to be taken care of and subtle bugs which can easily creep in.

Sadly, it is another road block for JavaFX development; only few developers will bite the bullet and go this path; but since you are reading this you belong to this elitist circle ;-)

## Links

- https://stackoverflow.com/questions/27628385/write-call-swift-code-using-java-s-jni - how to call swift code from Java
- https://developer.apple.com/documentation/security/app_sandbox/accessing_files_from_the_macos_app_sandbox - API to call
- https://schlining.medium.com/a-simple-java-native-interface-jni-example-in-java-and-scala-68fdafe76f5f example on how to use scala & java and JNI