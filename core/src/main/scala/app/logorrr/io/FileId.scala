package app.logorrr.io

import pureconfig.{ConfigReader, ConfigWriter}

import java.nio.file.{Path, Paths}

object FileId {

  implicit lazy val reader: ConfigReader[FileId] = ConfigReader[String].map(s => FileId(s))
  implicit lazy val writer: ConfigWriter[FileId] = ConfigWriter[String].contramap(c => c.value)

  def apply(p: Path): FileId = {
    FileId(p.toAbsolutePath.toString)
  }

}

/**
 * Identifies a log file
 *
 * @param value is an identifier which is used to discriminate between log files. typically it is a file path.
 */
case class FileId(value: String) {

  def asPath : Path = Paths.get(value)

  def fileName: String = asPath.getFileName.toString

  def absolutePathAsString : String = value

}
