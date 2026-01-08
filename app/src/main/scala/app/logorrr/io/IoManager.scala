package app.logorrr.io

import app.logorrr.conf.{FileId, TimestampSettings}
import app.logorrr.model.LogEntry
import javafx.collections.{FXCollections, ObservableList}
import net.ladstatt.util.log.CanLog
import net.ladstatt.util.os.OsUtil

import java.io._
import java.nio.file.{Files, Path}
import java.time.{Duration, Instant}
import java.util
import java.util.zip.{ZipEntry, ZipInputStream}
import scala.util.{Failure, Success, Try}


object IoManager extends CanLog:

  private def mkReader(path: Path): BufferedReader =
    val encoding = FEncoding(path)
    if encoding == Unknown then
      new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile), UTF8.asString))
    else
      new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile), encoding.asString))

  private def mkReader(bytes: Array[Byte]): BufferedReader =
    val encoding = FEncoding(bytes)
    if encoding == Unknown then
      new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), UTF8.asString))
    else
      new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), encoding.asString))

  def fromPath(path: Path): Seq[String] =
    require(Files.exists(path))
    toSeq(mkReader(path))

  private def toSeq(reader: BufferedReader): Seq[String] =
    try
      (for line <- Iterator.continually(reader.readLine()).takeWhile(_ != null) yield line).toSeq
    finally
      reader.close()

  private def fromPathUsingSecurityBookmarks(logFile: Path): Seq[String] =
    OsxBridgeHelper.registerPath(logFile)
    val lines = IoManager.fromPath(logFile)
    if lines.isEmpty then
      logWarn(s"${logFile.toAbsolutePath.toString} was empty.")
    lines

  def from(asBytes: Array[Byte]): ObservableList[LogEntry] =
    var lineNumber: Int = 0
    val arraylist = new java.util.ArrayList[LogEntry]()
    toSeq(mkReader(asBytes)).map(l => {
      lineNumber = lineNumber + 1
      arraylist.add(LogEntry(lineNumber, l, None, None))
    })
    FXCollections.observableList(arraylist)


  def from(logFile: Path): ObservableList[LogEntry] =
    var lineNumber: Int = 0
    val arraylist = new java.util.ArrayList[LogEntry]()
    fromPathUsingSecurityBookmarks(logFile).map(l => {
      lineNumber = lineNumber + 1
      arraylist.add(LogEntry(lineNumber, l, None, None))
    })
    FXCollections.observableList(arraylist)

  def from(logFile: Path
           , logEntryTimeFormat: TimestampSettings): ObservableList[LogEntry] =
    var lineNumber: Int = 0
    var someFirstEntryTimestamp: Option[Instant] = None
    val arraylist = new util.ArrayList[LogEntry]()
    fromPathUsingSecurityBookmarks(logFile).map(l => {
      lineNumber = lineNumber + 1
      val someInstant: Option[Instant] = TimestampSettings.parseInstant(l, logEntryTimeFormat)
      if someFirstEntryTimestamp.isEmpty then {
        someFirstEntryTimestamp = someInstant
      }

      val someDiffFromStart: Option[Duration] = for
        firstEntry <- someFirstEntryTimestamp
        instant <- someInstant
      yield Duration.between(firstEntry, instant)
      val entry = LogEntry(lineNumber, l, someInstant, someDiffFromStart)
      arraylist.add(entry)
    })
    FXCollections.observableList(arraylist)

  def readEntries(path: Path
                  , someLogEntryInstantFormat: Option[TimestampSettings]): ObservableList[LogEntry] =
    if isPathValid(path) then
      Try(someLogEntryInstantFormat match {
        case None => IoManager.from(path)
        case Some(instantFormat) => from(path, instantFormat)
      }) match
        case Success(logEntries) => logEntries
        case Failure(ex) =>
          val msg = s"Could not load file ${path.toAbsolutePath.toString}"
          logException(msg, ex)
          FXCollections.observableArrayList()
    else
      logWarn(s"Could not read ${path.toAbsolutePath.toString} - does it exist?")
      FXCollections.observableArrayList()

  def isPathValid(path: Path): Boolean =
    if OsUtil.isMac then
      Files.exists(path)
    else
      // without security bookmarks initialized, this returns false on mac
      Files.isReadable(path) && Files.isRegularFile(path)

  /**
   * Given a zip file, extract its contents recursively and return all files contained in a map. the key of this
   * map represents the file name in the zip file, and the value is the contents of the file as string.
   *
   * @param zipFile       the zip file
   * @param relevantFiles if empty, return all files, else only those which match given file ids
   * @return
   */
  def unzip(zipFile: Path, relevantFiles: Set[FileId] = Set()): Map[FileId, ObservableList[LogEntry]] =
    OsxBridgeHelper.registerPath(zipFile)
    var resultMap: Map[FileId, ObservableList[LogEntry]] = Map()
    try
      val zipIn = new ZipInputStream(Files.newInputStream(zipFile))
      var entry: ZipEntry = zipIn.getNextEntry
      while entry != null do
        if !entry.isDirectory then
          // by convention reference a file which is contained in a zip file like that
          val id = FileId(zipFile.toAbsolutePath.toString + "@" + entry.getName)
          if relevantFiles.isEmpty || relevantFiles.contains(id) then // do not read all entries if there is no need to do it
            resultMap += (id -> IoManager.from(zipIn.readAllBytes()))
        zipIn.closeEntry()
        entry = zipIn.getNextEntry
      zipIn.close()
    catch
      case e: IOException => logException("I/O error during unzip", e)
    resultMap


  def isZip(path: Path): Boolean = path.getFileName.toString.endsWith(".zip")

