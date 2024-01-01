package app.logorrr.io

import app.logorrr.util.CanLog

import java.io.IOException
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

/**
 * File related operations
 */
object Fs extends CanLog {

  /** compute file name from a LogFilePath */
  def logFileName(fileId: FileId): String = fileId.fileName

  def createDirectories(path: Path): Unit = {
    if (Files.exists(path)) {
      logTrace(s"Using directory '${path.toAbsolutePath.toString}'")
    } else {
      Files.createDirectories(path)
      logTrace(s"Created directory '${path.toAbsolutePath.toString}'")
    }
  }

  def write(path: Path, content: String): Unit = timeR({
    createDirectories(path.getParent)
    Files.write(path, content.getBytes(Charset.forName("UTF-8")))
  }, s"Wrote '${path.toAbsolutePath.toString}'")

}
