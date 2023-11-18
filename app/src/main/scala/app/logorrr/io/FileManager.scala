package app.logorrr.io

import app.logorrr.OsxBridge
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, OsUtil}
import javafx.collections.{FXCollections, ObservableList}

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.nio.file.{Files, Path}


object FileManager extends CanLog {

  private def openFileWithDetectedEncoding(path: Path): BufferedReader = {
    val encoding = FEncoding(path)
    if (encoding == Unknown) {
      new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile), UTF8.asString))
    } else {
      new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile), encoding.asString))
    }
  }

  def fromPath(path: Path): Seq[String] = {
    require(Files.exists(path))
    val reader = openFileWithDetectedEncoding(path)
    try {
      (for (line <- Iterator.continually(reader.readLine()).takeWhile(_ != null)) yield line).toSeq
    } finally {
      reader.close()
    }
  }

  def fromPathUsingSecurityBookmarks(logFile: Path): Seq[String] = {
    if (OsUtil.enableSecurityBookmarks) {
      logInfo(s"Registering security bookmark for `${logFile.toAbsolutePath.toString}`")
      OsxBridge.registerPath(logFile.toAbsolutePath.toString)
    }
    val lines = FileManager.fromPath(logFile)
    if (lines.isEmpty) {
      logWarn(s"${logFile.toAbsolutePath.toString} was empty.")
    }
    lines
  }

  def from(logFile: Path): ObservableList[LogEntry] = {
    var lineNumber: Int = 0
    val arraylist = new java.util.ArrayList[LogEntry]()
    fromPathUsingSecurityBookmarks(logFile).map(l => {
      lineNumber = lineNumber + 1
      arraylist.add(LogEntry(lineNumber, l, None))
    })
    FXCollections.observableList(arraylist)
  }

}
