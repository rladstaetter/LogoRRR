package app.logorrr.conf.mut

import app.logorrr.conf.*
import app.logorrr.util.PersistenceManager
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.{Property, SimpleBooleanProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.stage.Window
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path
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

  /** contains mutable information for the application stage */
  val mutStageSettings = new MutStageSettings

  /** global container for search term groups */
  val mutSearchTermGroupSettings = new MutSearchTermGroupSettings

  /** tracks which log file is active */
  val someActiveLogProperty: SimpleObjectProperty[Option[FileId]] = new SimpleObjectProperty[Option[FileId]](None)

  /** settings can be either all undefined (None) or have some value */
  val timeSettings = new MutTimeSettings

  /** remembers last opened directory for the next execution */
  val lastUsedDirectoryProperty = new SimpleObjectProperty[Option[Path]](None)

  private val mapDirtyPulse = new SimpleBooleanProperty(false)

  /**
   * Contains mutable state information for all log files, sorted after firstOpened
   *
   * */
  // TODO remove listener
  private val mutLogFileSettings = FXCollections.observableList(new java.util.ArrayList[MutLogFileSettings]())
  mutLogFileSettings.addListener(_ => mapDirtyPulse.set(!mapDirtyPulse.get))


  def contains(fileId: FileId): Boolean = mutLogFileSettings.stream.anyMatch(_.getFileId.equals(fileId))


  def set(persistenceManager: PersistenceManager, settings: Settings): Unit =
    setStageSettings(settings.stageSettings)
    setLogFileSettings(persistenceManager, settings.fileSettings)
    setSomeLastUsedDirectory(settings.someLastUsedDirectory)
    // Populate from immutable settings into the mutable properties
    mutSearchTermGroupSettings.searchTermGroupEntries.setAll(
      settings.searchTermGroups.map(MutSearchTermGroup.apply).asJava
    )

  def getSomeActiveLogFile: Option[FileId] = someActiveLogProperty.get()

  def setTimeSettings(settings: MutTimeSettings): Unit = timeSettings.set(settings)

  def getSomeLastUsedDirectory: Option[Path] = lastUsedDirectoryProperty.get()

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = lastUsedDirectoryProperty.set(someDirectory)

  def add(stg: MutSearchTermGroup): Unit = mutSearchTermGroupSettings.add(stg)

  def clearSearchTermGroups(): Unit = mutSearchTermGroupSettings.clear()

  def getMutLogFileSetting(key: FileId): MutLogFileSettings =
    mutLogFileSettings.stream.filter(_.getFileId.equals(key)).findFirst().get

  def add(settings: MutLogFileSettings): Unit = this.mutLogFileSettings.add(settings)

  def removeLogFile(fileId: FileId): Unit = mutLogFileSettings.removeIf(_.getFileId == fileId)

  def setLogFileSettings(persistenceManager: PersistenceManager, logFileSettings: Map[String, LogFileSettings]): Unit = {
    mutLogFileSettings.clear()
    val sortedByFirstOpened: Seq[LogFileSettings] = logFileSettings.values.toSeq.sortWith((a, b) => a.firstOpened < b.firstOpened)

    for (settings <- sortedByFirstOpened) {
      val s = MutLogFileSettings(settings)
      persistenceManager.init(s.getFileId, s.allProps)
      mutLogFileSettings.add(s)
    }
  }

  def clearLogFileSettings(): Unit = mutLogFileSettings.clear()

  def setStageSettings(stageSettings: StageSettings): Unit = mutStageSettings.set(stageSettings)

  def bindWindowProperties(window: Window): Unit =
    mutStageSettings.bindWindowProperties(
      window.xProperty()
      , window.yProperty()
      , window.getScene.widthProperty()
      , window.getScene.heightProperty().add(MutSettings.WindowHeightHack)
    )

  def unbindWindow(): Unit = mutStageSettings.unbindWindow()

  def getStageY: Double = mutStageSettings.getY

  def getStageX: Double = mutStageSettings.getX

  def getStageHeight: Int = mutStageSettings.getHeight

  def getStageWidth: Int = mutStageSettings.getWidth

  def fileIds: ObjectBinding[Set[FileId]] = new ObjectBinding[Set[FileId]] {
    bind(mutLogFileSettings)

    override def computeValue(): Set[FileId] = mutLogFileSettings.stream.map(_.getFileId).toList.asScala.toSet
  }

  def getMutLogFileSettings: ObservableList[MutLogFileSettings] = mutLogFileSettings


  def mkImmutable(): Settings =
    val logFileSettings: Map[String, LogFileSettings] =
      (for s <- mutLogFileSettings.asScala yield {
        s.getFileId.absolutePathAsString -> s.mkImmutable()
      }).toMap

    Settings(mutStageSettings.mkImmutable()
      , logFileSettings
      , getSomeActiveLogFile
      , getSomeLastUsedDirectory
      , mutSearchTermGroupSettings.mkImmutable()
      , if timeSettings.validBinding.get() then Option(timeSettings.mkImmutable()) else None)


  val allProps: Set[Property[?]] =
    mutStageSettings.allProps ++
      mutSearchTermGroupSettings.allProps ++
      timeSettings.allProps ++
      Seq(someActiveLogProperty, lastUsedDirectoryProperty, mapDirtyPulse)
}