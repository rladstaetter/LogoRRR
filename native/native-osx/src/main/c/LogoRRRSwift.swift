import Foundation
import Cocoa

@_cdecl("openUrl")
public func openUrl(path : UnsafePointer<Int8>) {
    if let urlString = String(validatingUTF8: path) {
        if let url = URL(string: urlString) {
           NSWorkspace.shared.open(url)
        }
    }
}


@_cdecl("releasePath")
public func releasePath(path: UnsafePointer<Int8>) {
    if let pathString = String(validatingUTF8: path) {
        let url =  URL(fileURLWithPath: pathString)
        // print("releasing: \(url.absoluteString)")
        url.stopAccessingSecurityScopedResource()
        UserDefaults.standard.removeObject(forKey: pathString)
    } else {
       // print("error releasing path!")
    }
}

@_cdecl("registerPath")
public func registerPath(path: UnsafePointer<Int8>) {
    if let pathString = String(validatingUTF8: path) {
        let url =  URL(fileURLWithPath: pathString)
        // print("URL: \(url.absoluteString)")
        if let bookmarkData = UserDefaults.standard.data(forKey: url.absoluteString) {
            // print("found in UserDefaults: \(url.absoluteString)")
            if let url = restoreAccess(forBookmark: bookmarkData) {
                // print("Restored access \(url.absoluteString)")
                // ... Perform other operations with the URL ...
                // Don't forget to release the security access when done
                // url.stopAccessingSecurityScopedResource()
            } else {
               // print("Could not restore access \(url.absoluteString)")
            }
        } else {
            // not found in UserDefaults, create one
            // print("not found in UserDefaults: \(url.absoluteString)")
            if let bookmarkData = createBookmark(for: url) {
                if let url = restoreAccess(forBookmark: bookmarkData) {
                    // print("Creating new entry \(url.absoluteString)")
                    storeBookmarkInUserDefaults(bookmarkData, forKey: url.absoluteString)
                } else {
                    // print("Error when trying to restore Access \(pathString)")
                }
            } else {
                // print("Error creating bookmark for \(pathString)")
            }
        }
    }
}

func createBookmark(for url: URL) -> Data? {
    do {
        let bookmarkData = try url.bookmarkData(options: .withSecurityScope, includingResourceValuesForKeys: nil, relativeTo: nil)
        return bookmarkData
    } catch let error as NSError {
        // print("Error creating bookmark: \(error.localizedDescription)")
        // print("Error code: \(error.code)")
        return nil
    }
}



func restoreAccess(forBookmark bookmarkData: Data) -> URL? {
    do {
        var isStale = false
        let url = try URL(resolvingBookmarkData: bookmarkData, options: .withSecurityScope, relativeTo: nil, bookmarkDataIsStale: &isStale)

        if isStale {
            // print("Bookmark data is stale.")
            return nil
        }

        if url.startAccessingSecurityScopedResource() {
            // print("Using resource \(url.absoluteString)")
            return url
        } else {
            // print("Failed to access resource.")
            return nil
        }
    } catch {
        // print("Error restoring bookmark: \(error)")
        return nil
    }
}

func storeBookmarkInUserDefaults(_ bookmarkData: Data, forKey key: String) {
    UserDefaults.standard.set(bookmarkData, forKey: key)
}

func restoreBookmarkFromUserDefaults(forKey key: String) -> Data? {
    return UserDefaults.standard.data(forKey: key)
}