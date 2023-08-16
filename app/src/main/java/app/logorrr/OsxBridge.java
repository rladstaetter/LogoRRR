package app.logorrr;

/**
 * Provides interface methods for native code
 */
public class OsxBridge {

    public static native void registerPath(String path);

    public static native void releasePath(String path);

}
