package app.logorrr.conf.mut

import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.model.LogFileSettings
import javafx.beans.property.{SimpleMapProperty, SimpleObjectProperty}
import javafx.collections.FXCollections
import javafx.stage.Window

import java.nio.file.Path
import java.util
import scala.jdk.CollectionConverters._

// This avoids passing around references to settings in all classes.
// This approach is some sort of experiment and the current state of my knowledge to cope with
// this problem when doing this sort of stuff in JavaFX. Happy to get input on how to solve the global
// configuration problem any better.
object MutSettings {

  def apply(settings: Settings): MutSettings = {
    val s = new MutSettings
    s.setStageSettings(settings.stageSettings)
    s.setLogFileSettings(settings.logFileSettings)
    s.setSomeActive(settings.someActive)
    s
  }

}


class MutSettings {

  /** remembers last opened directory for the next execution */
  val lastUsedDirectoryProperty = new SimpleObjectProperty[Option[Path]](None)

  def getSomeLastUsedDirectory: Option[Path] = {
    lastUsedDirectoryProperty.get()
  }

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = {
    lastUsedDirectoryProperty.set(someDirectory)
  }

  /** contains mutable information for the application stage */
  private val mutStageSettings = new MutStageSettings

  /** contains mutable state information for all log files */
  private val mutLogFileSettingsMapProperty = new SimpleMapProperty[String, MutLogFileSettings](FXCollections.observableMap(new util.HashMap()))

  def isEmpty = mutLogFileSettingsMapProperty.isEmpty

  /** tracks which log file is active */
  private val someActiveLogProperty = new SimpleObjectProperty[Option[String]](None)

  def getMutLogFileSetting(key: String): MutLogFileSettings = mutLogFileSettingsMapProperty.get(key)

  def putMutLogFileSetting(mutLogFileSettings: MutLogFileSettings): Unit = {
    mutLogFileSettingsMapProperty.put(mutLogFileSettings.getPathAsString(), mutLogFileSettings)
  }

  def removeLogFileSetting(pathAsString: String): Unit = mutLogFileSettingsMapProperty.remove(pathAsString)

  def set(settings: Settings): Unit = {
    setStageSettings(settings.stageSettings)
    setLogFileSettings(settings.logFileSettings)
    setSomeActive(settings.someActive)
    setSomeLastUsedDirectory(settings.someLastUsedDirectory)
  }

  def setSomeActive(path: Option[String]): Unit = someActiveLogProperty.set(path)

  def getSomeActive: Option[String] = someActiveLogProperty.get()

  def setLogFileSettings(logFileSettings: Map[String, LogFileSettings]): Unit = {
    val m = for ((k, v) <- logFileSettings) yield {
      k -> MutLogFileSettings(v)
    }
    mutLogFileSettingsMapProperty.putAll(m.asJava)
  }

  def petrify(): Settings = {
    val logFileSettings: Map[String, LogFileSettings] = (for ((k, v) <- mutLogFileSettingsMapProperty.get.asScala) yield {
      k -> v.petrify()
    }).toMap
    Settings(mutStageSettings.petrify(), logFileSettings, getSomeActive, getSomeLastUsedDirectory)
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
    mutStageSettings.heightProperty.bind(window.getScene.heightProperty())
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

  def getStageWidth: Int = mutStageSettings.getWidth()

  def getOrderedLogFileSettings: Seq[LogFileSettings] = {
    mutLogFileSettingsMapProperty.get().values.asScala.toSeq.sortWith((lt, gt) => lt.getFirstOpened() < gt.getFirstOpened()).map(_.petrify())
  }


}