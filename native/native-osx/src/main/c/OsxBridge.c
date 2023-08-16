#include <jni.h>
#include <stdio.h>
#include "app_logorrr_OsxBridge.h"
#include "app_logorrr_OsxBridge_swift.h"

JNIEXPORT void JNICALL Java_app_logorrr_OsxBridge_registerPath(JNIEnv *env, jobject obj, jstring path) {
    const char *pathChars = (*env)->GetStringUTFChars(env, path, NULL);
     if (pathChars != NULL) {
         // Call the Swift function from C
         registerPath(pathChars);

         // Release the allocated memory
         (*env)->ReleaseStringUTFChars(env, path, pathChars);
     }
}


JNIEXPORT void JNICALL Java_app_logorrr_OsxBridge_releasePath(JNIEnv *env, jobject obj, jstring path) {
    const char *pathChars = (*env)->GetStringUTFChars(env, path, NULL);
     if (pathChars != NULL) {
         releasePath(pathChars);
         (*env)->ReleaseStringUTFChars(env, path, pathChars);
     }
}
