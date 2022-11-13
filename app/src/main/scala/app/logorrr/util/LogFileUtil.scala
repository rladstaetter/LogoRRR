package app.logorrr.util

import java.nio.file.Paths

object LogFileUtil {

  /** compute file name from a LogFilePath */
  def logFileName(pathAsString: String): String =
    Paths.get(pathAsString).getFileName.toString

}
