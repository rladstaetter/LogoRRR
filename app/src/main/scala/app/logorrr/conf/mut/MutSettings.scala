package app.logorrr.conf.mut

import app.logorrr.conf.*
import javafx.beans.property.{SimpleMapProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.stage.Window
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path
import java.util
import scala.jdk.CollectionConverters.*

object MutSettings:

  /**
   * due to glorious app logic we need this constant to add to our windows height calculation
   *
   * will not work always exactly, depending on user settings/skins - has to be improved ...
   * */
  val WindowHeightHack: Int =
    if OsUtil.isMac then 28
    else if OsUtil.isLinux then 37
    else if OsUtil.isWin then 38
    else 28


class MutSettings {

  /** settings can be either all undefined (None) or have some value */
  private val timeStampSettingsProperty = new SimpleObjectProperty[MutTimestampSettings]()

  def setTimestampSettings(settings: MutTimestampSettings) = timeStampSettingsProperty.set(settings)

  def getTimestampSettings(): MutTimestampSettings = timeStampSettingsProperty.get()

  /** global container for search term groups */
  val mutSearchTermGroupSettings = new MutSearchTermGroupSettings

  def searchTermGroupNames: ObservableList[String] = mutSearchTermGroupSettings.searchTermGroupNames

  /** remembers last opened directory for the next execution */
  val lastUsedDirectoryProperty = new SimpleObjectProperty[Option[Path]](None)

  def getSomeLastUsedDirectory: Option[Path] = lastUsedDirectoryProperty.get()

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit =
    lastUsedDirectoryProperty.set(someDirectory)

  /** contains mutable information for the application stage */
  private val mutStageSettings = new MutStageSettings


  /** contains mutable state information for all log files */
  private val mutLogFileSettingsMapProperty = new SimpleMapProperty[FileId, MutLogFileSettings](FXCollections.observableMap(new util.HashMap()))

  def putSearchTermGroup(stg: SearchTermGroup): Unit = mutSearchTermGroupSettings.put(stg.name, stg.terms)

  def clearSearchTermGroups(): Unit = mutSearchTermGroupSettings.clear()

  def removeSearchTermGroup(name: String): Unit = mutSearchTermGroupSettings.remove(name)

  /** tracks which log file is active */
  private val someActiveLogProperty = new SimpleObjectProperty[Option[FileId]](None)

  def getMutLogFileSetting(key: FileId): MutLogFileSettings =
    mutLogFileSettingsMapProperty.get(key)

  def putMutLogFileSetting(mutLogFileSettings: MutLogFileSettings): Unit =
    mutLogFileSettingsMapProperty.put(mutLogFileSettings.getFileId, mutLogFileSettings)

  def removeLogFileSetting(fileId: FileId): Unit = mutLogFileSettingsMapProperty.remove(fileId)

  def setSomeActive(path: Option[FileId]): Unit = someActiveLogProperty.set(path)

  def getSomeActiveLogFile: Option[FileId] = someActiveLogProperty.get()

  def setLogFileSettings(logFileSettings: Map[String, LogFileSettings]): Unit =
    val m = for (k, settings) <- logFileSettings yield
      FileId(k) -> MutLogFileSettings(settings)
    mutLogFileSettingsMapProperty.putAll(m.asJava)

  def mkImmutable(): Settings =
    val logFileSettings: Map[String, LogFileSettings] = (for (k, v) <- mutLogFileSettingsMapProperty.get.asScala yield {
      k.absolutePathAsString -> v.mkImmutable()
    }).toMap
    Settings(mutStageSettings.mkImmutable()
      , logFileSettings
      , getSomeActiveLogFile
      , getSomeLastUsedDirectory
      , mutSearchTermGroupSettings.mkImmutable()
      , Option(getTimestampSettings()).map(_.mkImmutable()))

  def setStageSettings(stageSettings: StageSettings): Unit =
    mutStageSettings.setX(stageSettings.x)
    mutStageSettings.setY(stageSettings.y)
    mutStageSettings.setHeight(stageSettings.height)
    mutStageSettings.setWidth(stageSettings.width)

  def clearLogFileSettings(): Unit =
    mutLogFileSettingsMapProperty.clear()
    setSomeActive(None)

  def bindWindowProperties(window: Window): Unit =
    mutStageSettings.bind(
      window.xProperty()
      , window.yProperty()
      , window.getScene.widthProperty()
      , window.getScene.heightProperty().add(MutSettings.WindowHeightHack)
    )

  def unbindWindow(): Unit =
    mutStageSettings.unbind()

  def getStageY: Double = mutStageSettings.getY

  def getStageX: Double = mutStageSettings.getX

  def getStageHeight: Int = mutStageSettings.getHeight

  def getStageWidth: Int = mutStageSettings.getWidth

  def getOrderedLogFileSettings: Seq[LogFileSettings] =
    val seq = mutLogFileSettingsMapProperty.get().values.asScala.toSeq
    seq.sortWith((lt, gt) => lt.getFirstOpened < gt.getFirstOpened).map(_.mkImmutable())


}