package app.logorrr.model

import app.logorrr.OsxBridge
import app.logorrr.io.FileManager
import app.logorrr.util.{CanLog, OsUtil}

import java.nio.file.Path

object LogFileReader extends CanLog {

  def readFromFile(logFile: Path): Seq[String] = {
    if (OsUtil.enableSecurityBookmarks) {
      logInfo(s"Registering security bookmark for ${logFile.toAbsolutePath.toString}")
      OsxBridge.registerPath(logFile.toAbsolutePath.toString)
    }
    val lines = FileManager.fromPath(logFile)
    logEmptyLogFile(logFile, lines)
    lines
  }

  private def logEmptyLogFile(logFile: Path, lines: Seq[String]): Unit = {
    if (lines.isEmpty) {
      logWarn(s"${logFile.toAbsolutePath.toString} was empty.")
    } else {
      // logTrace(s"${logFile.toAbsolutePath.toString} has ${lines.size} lines.")
    }
  }
}
