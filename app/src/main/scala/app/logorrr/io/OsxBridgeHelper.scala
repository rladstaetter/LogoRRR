package app.logorrr.io

import app.logorrr.OsxBridge
import app.logorrr.util.{CanLog, OsUtil}

import java.nio.file.Path

object OsxBridgeHelper extends CanLog {

  def registerPath(path: Path): Unit = {
    if (OsUtil.enableSecurityBookmarks) {
      logInfo(s"Registering security bookmark for '${path.toAbsolutePath.toString}'")
      OsxBridge.registerPath(path.toAbsolutePath)
    }
  }

  def releasePath(path: Path): Unit = {
    if (OsUtil.enableSecurityBookmarks) {
      logInfo(s"Releasing security bookmark for '${path.toAbsolutePath.toString}'")
      OsxBridge.releasePath(path)
    }
  }
}
