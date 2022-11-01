package app.logorrr.model

import app.logorrr.util.CanLog

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util
import scala.util.{Failure, Success, Try}

object LogFileReader extends CanLog {

  def readFromFile(logFile: Path): util.List[String] = {
    Try {
      Files.readAllLines(logFile)
    } match {
      case Failure(exception) =>
        val msg = s"Could not read file ${logFile.toAbsolutePath.toString} with default charset, retrying with ISO_8859_1 ..."
        logException(msg, exception)
        Try {
          Files.readAllLines(logFile, StandardCharsets.ISO_8859_1)
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


}
