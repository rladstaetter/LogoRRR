package app.logorrr.io

import app.logorrr.OsxBridge
import net.ladstatt.util.log.CanLog
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path

object OsxBridgeHelper extends CanLog {

  def registerPath(path: Path): Unit = {
    if (OsUtil.enableSecurityBookmarks) {
      logTrace(s"Registering security bookmark for '${path.toAbsolutePath.toString}'")
      OsxBridge.registerPath(path.toAbsolutePath)
    }
  }

  def releasePath(path: Path): Unit = {
    if (OsUtil.enableSecurityBookmarks) {
      logTrace(s"Releasing security bookmark for '${path.toAbsolutePath.toString}'")
      OsxBridge.releasePath(path)
    }
  }
}
