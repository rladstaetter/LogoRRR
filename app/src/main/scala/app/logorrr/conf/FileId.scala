package app.logorrr.conf

import upickle.default.*

import java.nio.file.{Path, Paths}

object FileId:

  given rw: ReadWriter[FileId] = readwriter[String].bimap(_.value, FileId(_))

  def apply(p: Path): FileId = FileId(p.toAbsolutePath.toString)

  def reduceZipFiles(fileIds: Seq[FileId]): Map[FileId, Seq[FileId]] = fileIds.groupBy(_.extractZipFileId)


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
  def zipEntryPath: String =
    extractZipFileId.fileName + value.substring(value.indexOf(".zip@") + 4, value.length)

  def isZipEntry: Boolean = value.contains(".zip@")

  def asPath: Path = Paths.get(value)

  def fileName: String = asPath.getFileName.toString

  def absolutePathAsString: String = value

}