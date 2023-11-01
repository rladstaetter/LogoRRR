package app.logorrr.io

import app.logorrr.util.CanLog

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

/**
 * File related operations
 */
object Fs extends CanLog {

  def createDirectories(path: Path): Unit = {
    if (Files.exists(path)) {
      logTrace(s"Using directory ${path.toAbsolutePath.toString}")
    } else {
      Files.createDirectories(path)
      logTrace(s"Created directory ${path.toAbsolutePath.toString}")
    }
  }

  def write(path: Path, content: String): Unit = timeR({
    createDirectories(path.getParent)
    Files.write(path, content.getBytes(Charset.forName("UTF-8")))
  }, s"Wrote ${path.toAbsolutePath}")

}
