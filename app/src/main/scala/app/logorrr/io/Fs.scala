package app.logorrr.io

import app.logorrr.util.CanLog

import java.nio.file.{Files, Path}

object Fs extends CanLog {

  def write(path: Path, content: String): Unit = timeR({
    Files.createDirectories(path.getParent)
    Files.write(path, content.getBytes("UTF-8"))
  }, s"Wrote ${path.toAbsolutePath}")

}
