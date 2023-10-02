package app.logorrr.io

import app.logorrr.util.CanLog

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.nio.file.{Files, Path}


object FileManager extends CanLog {

  private def openFileWithDetectedEncoding(path: Path): BufferedReader = {
    val encoding = FEncoding(path)
    if (encoding == Unknown) {
      logTrace(s"${encoding.asString} encoding - fallback to UTF-8")
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

}
