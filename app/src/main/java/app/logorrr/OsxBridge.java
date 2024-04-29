package app.logorrr;

import java.nio.file.Path;

/**
 * Provides interface methods for native code
 */
public class OsxBridge {
    /**
     * register a file in the macosx permission system
     *
     * @param path absolute path to the file
     */
    public static native void registerPath(String path);

    /**
     * release a path (if the path is no longer used for example)
     *
     * @param path absolute path to the file
     */
    public static native void releasePath(String path);

    /**
     * open an url via the native browser
     *
     * @param url the url to open, for example https://www.logorrr.app/
     */
    public static native void openUrl(String url);

    public static void registerPath(Path path) {
        if (path != null) {
            registerPath(path.toAbsolutePath().toString());
        }
    }

    public static void releasePath(Path path) {
        if (path != null) {
            releasePath(path.toAbsolutePath().toString());
        }
    }
}
