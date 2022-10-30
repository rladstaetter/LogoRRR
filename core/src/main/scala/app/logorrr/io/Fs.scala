package app.logorrr.io

import app.logorrr.util.CanLog

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

object Fs extends CanLog {

  def createDirectories(path: Path): Unit = {
    if (Files.exists(path)) {
      logTrace("Using directory " + path.toAbsolutePath.toString)
    } else {
      Files.createDirectories(path)
      logTrace("Created directory " + path.toAbsolutePath.toString)
    }
  }

  def write(path: Path, content: String): Unit = timeR({
    createDirectories(path.getParent)
    Files.write(path, content.getBytes(Charset.forName("UTF-8")))
  }, s"Wrote ${path.toAbsolutePath}")

}
