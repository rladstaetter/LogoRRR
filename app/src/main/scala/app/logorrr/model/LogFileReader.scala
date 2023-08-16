package app.logorrr.model

import app.logorrr.OsxBridge
import app.logorrr.util.{CanLog, OsUtil}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util
import scala.util.{Failure, Success, Try}

object LogFileReader extends CanLog {

  def readFromFile(logFile: Path): util.List[String] = {
    Try {
      if (OsUtil.isMac) {
        logInfo("Registering security bookmark for " + logFile.toAbsolutePath.toString)
        OsxBridge.registerPath(logFile.toAbsolutePath.toString)
      }

      val lines = Files.readAllLines(logFile)

      logEmptyLogFile(logFile, lines)
      lines
    } match {
      case Failure(exception) =>
        val msg = s"Failed to read ${logFile.toAbsolutePath.toString}, exception: ${exception.getMessage}, retrying ISO_8859_1 ..."
        logException(msg, exception)
        Try {
          val lines = Files.readAllLines(logFile, StandardCharsets.ISO_8859_1)
          logEmptyLogFile(logFile, lines)
          lines
        } match {
          case Failure(exception) =>
            val msg = s"Could not read file ${logFile.toAbsolutePath.toString} properly. Reason: ${exception.getMessage}."
            logException(msg, exception)
            util.Arrays.asList(msg)
          case Success(value) =>
            value
        }
      case Success(lines) => lines
    }
  }


  private def logEmptyLogFile(logFile: Path, lines: util.List[String]): Unit = {
    if (lines.isEmpty) {
      logWarn(s"${logFile.toAbsolutePath.toString} was empty.")
    } else {
      logTrace(s"${logFile.toAbsolutePath.toString} has ${lines.size()} lines.")
    }
  }
}
