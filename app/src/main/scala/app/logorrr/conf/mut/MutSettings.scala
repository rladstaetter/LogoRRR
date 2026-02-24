package app.logorrr.conf.mut

import app.logorrr.conf.*
import app.logorrr.conf.LogoRRRGlobals.{mutSettings, setHostServices, setTimestampSettings}
import javafx.beans.property.{SimpleMapProperty, SimpleObjectProperty}
import javafx.collections.FXCollections
import javafx.beans.property.{Property, SimpleBooleanProperty, SimpleMapProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, MapChangeListener}
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

  /** contains mutable information for the application stage */
  val mutStageSettings = new MutStageSettings

  /** global container for search term groups */
  val mutSearchTermGroupSettings = new MutSearchTermGroupSettings

  /** tracks which log file is active */
  val someActiveLogProperty: SimpleObjectProperty[Option[FileId]] = new SimpleObjectProperty[Option[FileId]](None)

  /** settings can be either all undefined (None) or have some value */
  private val timeStampSettingsProperty = new SimpleObjectProperty[MutTimestampSettings]()

  /** remembers last opened directory for the next execution */
  val lastUsedDirectoryProperty = new SimpleObjectProperty[Option[Path]](None)

  /** contains mutable state information for all log files */
  private val mutLogFileSettingsMapProperty = new SimpleMapProperty[FileId, MutLogFileSettings](FXCollections.observableMap(new util.HashMap()))

  private val mapDirtyPulse = new SimpleBooleanProperty(false)

  mutLogFileSettingsMapProperty.addListener(new MapChangeListener[FileId, MutLogFileSettings] {
    override def onChanged(change: MapChangeListener.Change[_ <: FileId, _ <: MutLogFileSettings]): Unit = {
      // If an entry is added, you might want to start listening to its internal properties too (see Step 2)
      mapDirtyPulse.set(!mapDirtyPulse.get())
    }
  })

  def set(settings: Settings): Unit =
    setStageSettings(settings.stageSettings)
    setLogFileSettings(settings.fileSettings)
    setSomeLastUsedDirectory(settings.someLastUsedDirectory)
    // Populate from immutable settings into the mutable properties
    mutSearchTermGroupSettings.searchTermGroupEntries.setAll(
      settings.searchTermGroups.map(MutSearchTermGroup.apply).asJava
    )


  def getSomeActiveLogFile: Option[FileId] = someActiveLogProperty.get()

  def setTimestampSettings(settings: MutTimestampSettings): Unit = timeStampSettingsProperty.set(settings)

  def getTimestampSettings: MutTimestampSettings = timeStampSettingsProperty.get()

  def getSomeLastUsedDirectory: Option[Path] = lastUsedDirectoryProperty.get()

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = lastUsedDirectoryProperty.set(someDirectory)
  def add(stg: MutSearchTermGroup): Unit = mutSearchTermGroupSettings.add(stg)

  def clearSearchTermGroups(): Unit = mutSearchTermGroupSettings.clear()

  // def removeSearchTermGroup(name: String): Unit = mutSearchTermGroupSettings.remove(name)

  def getMutLogFileSetting(key: FileId): MutLogFileSettings =
    mutLogFileSettingsMapProperty.get(key)

  def putMutLogFileSetting(mutLogFileSettings: MutLogFileSettings): Unit =
    mutLogFileSettingsMapProperty.put(mutLogFileSettings.getFileId, mutLogFileSettings)

  def removeLogFileSetting(fileId: FileId): Unit = mutLogFileSettingsMapProperty.remove(fileId)

  def setLogFileSettings(logFileSettings: Map[String, LogFileSettings]): Unit =
    val m = for (k, settings) <- logFileSettings yield
      FileId(k) -> MutLogFileSettings(settings)
    mutLogFileSettingsMapProperty.putAll(m.asJava)


  def clearLogFileSettings(): Unit = mutLogFileSettingsMapProperty.clear()

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

  def getOrderedLogFileSettings: Seq[LogFileSettings] =
    val seq = mutLogFileSettingsMapProperty.get().values.asScala.toSeq
    seq.sortWith((lt, gt) => lt.getFirstOpened < gt.getFirstOpened).map(_.mkImmutable())

  def mkImmutable(): Settings =
    val logFileSettings: Map[String, LogFileSettings] = (for (k, v) <- mutLogFileSettingsMapProperty.get.asScala yield {
      k.absolutePathAsString -> v.mkImmutable()
    }).toMap
    Settings(mutStageSettings.mkImmutable()
      , logFileSettings
      , getSomeActiveLogFile
      , getSomeLastUsedDirectory
      , mutSearchTermGroupSettings.mkImmutable()
      , Option(getTimestampSettings).map(_.mkImmutable()))


  val allProps: Set[Property[?]] =
    mutStageSettings.allProps ++
      mutSearchTermGroupSettings.allProps ++
        Seq(someActiveLogProperty, timeStampSettingsProperty, lastUsedDirectoryProperty, mapDirtyPulse)
}