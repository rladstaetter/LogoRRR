package app.logorrr.conf.mut

import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.io.FileId
import app.logorrr.model.LogFileSettings
import javafx.beans.property.{SimpleMapProperty, SimpleObjectProperty}
import javafx.collections.FXCollections
import javafx.stage.Window
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path
import java.util
import scala.jdk.CollectionConverters._

object MutSettings {

  /**
   * due to glorious app logic we need this constant to add to our windows height calculation
   *
   * will not work always exactly, depending on user settings/skins - has to be improved ...
   **/
  val WindowHeightHack: Int = {
    if (OsUtil.isMac) 28
    else if (OsUtil.isLinux) 37
    else if (OsUtil.isWin) 38
    else 28
  }
}

class MutSettings {


  /** remembers last opened directory for the next execution */
  val lastUsedDirectoryProperty = new SimpleObjectProperty[Option[Path]](None)

  def getSomeLastUsedDirectory: Option[Path] = lastUsedDirectoryProperty.get()

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = {
    lastUsedDirectoryProperty.set(someDirectory)
  }

  /** contains mutable information for the application stage */
  private val mutStageSettings = new MutStageSettings

  /** contains mutable state information for all log files */
  private val mutLogFileSettingsMapProperty = new SimpleMapProperty[FileId, MutLogFileSettings](FXCollections.observableMap(new util.HashMap()))

  /** tracks which log file is active */
  private val someActiveLogProperty = new SimpleObjectProperty[Option[FileId]](None)

  def getMutLogFileSetting(key: FileId): MutLogFileSettings = mutLogFileSettingsMapProperty.get(key)

  def putMutLogFileSetting(mutLogFileSettings: MutLogFileSettings): Unit = {
    mutLogFileSettingsMapProperty.put(mutLogFileSettings.getFileId, mutLogFileSettings)
  }

  def removeLogFileSetting(fileId: FileId): Unit = mutLogFileSettingsMapProperty.remove(fileId)

  def setSomeActive(path: Option[FileId]): Unit = someActiveLogProperty.set(path)

  def getSomeActiveLogFile: Option[FileId] = someActiveLogProperty.get()

  def setLogFileSettings(logFileSettings: Map[String, LogFileSettings]): Unit = {
    val m = for ((k, v) <- logFileSettings) yield {
      FileId(k) -> MutLogFileSettings(v)
    }
    mutLogFileSettingsMapProperty.putAll(m.asJava)
  }

  def mkImmutable(): Settings = {
    val logFileSettings: Map[String, LogFileSettings] = (for ((k, v) <- mutLogFileSettingsMapProperty.get.asScala) yield {
      k.absolutePathAsString -> v.mkImmutable()
    }).toMap
    Settings(mutStageSettings.mkImmutable(), logFileSettings, getSomeActiveLogFile, getSomeLastUsedDirectory)
  }

  def setStageSettings(stageSettings: StageSettings): Unit = {
    mutStageSettings.setX(stageSettings.x)
    mutStageSettings.setY(stageSettings.y)
    mutStageSettings.setHeight(stageSettings.height)
    mutStageSettings.setWidth(stageSettings.width)
  }

  def clearLogFileSettings(): Unit = {
    mutLogFileSettingsMapProperty.clear()
    setSomeActive(None)
  }

  def bindWindowProperties(window: Window): Unit = {
    mutStageSettings.widthProperty.bind(window.getScene.widthProperty())
    mutStageSettings.heightProperty.bind(window.getScene.heightProperty().add(MutSettings.WindowHeightHack))
    mutStageSettings.xProperty.bind(window.xProperty())
    mutStageSettings.yProperty.bind(window.yProperty())
  }

  def unbindWindow(): Unit = {
    mutStageSettings.widthProperty.unbind()
    mutStageSettings.heightProperty.unbind()
    mutStageSettings.xProperty.unbind()
    mutStageSettings.yProperty.unbind()
  }

  def getStageY: Double = mutStageSettings.yProperty.get()

  def getStageX: Double = mutStageSettings.xProperty.get()

  def getStageHeight: Int = mutStageSettings.heightProperty.get()

  def getStageWidth: Int = mutStageSettings.getWidth

  def getOrderedLogFileSettings: Seq[LogFileSettings] = {
    val seq = mutLogFileSettingsMapProperty.get().values.asScala.toSeq
    seq.sortWith((lt, gt) => lt.getFirstOpened < gt.getFirstOpened).map(_.mkImmutable())
  }


}