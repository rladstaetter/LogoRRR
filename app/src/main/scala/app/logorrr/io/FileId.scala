package app.logorrr.io

import pureconfig.{ConfigReader, ConfigWriter}

import java.nio.file.{Path, Paths}

object FileId {

  implicit lazy val reader: ConfigReader[FileId] = ConfigReader[String].map(s => FileId(s))
  implicit lazy val writer: ConfigWriter[FileId] = ConfigWriter[String].contramap(c => c.value)

  def apply(p: Path): FileId = {
    FileId(p.toAbsolutePath.toString)
  }

  def reduceZipFiles(fileIds: Seq[FileId]): Map[FileId, Seq[FileId]] = fileIds.groupBy(fileId => fileId.extractZipFileId)

}


/**
 * Identifies a log file or an entry in a zip file.
 *
 * If a value contains the string '.zip@', it is considered to be an zip file entry.
 *
 * @param value is an identifier which is used to discriminate between log files. typically it is a file path.
 */
case class FileId(value: String) {

  def extractZipFileId: FileId = FileId(value.substring(0, value.indexOf(".zip@") + 4)) // get filename of zip file

  // if fileId is a zip part, show this relative part
  def zipEntryPath: String = {
    extractZipFileId.fileName + value.substring(value.indexOf(".zip@") + 4, value.length)
  }

  def isZipEntry: Boolean = value.contains(".zip@")

  def asPath: Path = Paths.get(value)

  def fileName: String = asPath.getFileName.toString

  def absolutePathAsString: String = value

}
