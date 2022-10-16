package app.logorrr.conf.mut

import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.model.LogFileSettings
import javafx.beans.property.{SimpleMapProperty, SimpleObjectProperty}
import javafx.collections.FXCollections

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

  val stageSettings = new MutStageSettings
  val logFileSettingsProperty = new SimpleMapProperty[String, MutLogFileSettings](FXCollections.observableMap(new util.HashMap()))
  val someActiveLogProperty = new SimpleObjectProperty[Option[String]](None)

  def getLogFileSetting(key: String): MutLogFileSettings = logFileSettingsProperty.get(key)

  def putLogFileSetting(logFileSettings: LogFileSettings): Unit = logFileSettingsProperty.put(logFileSettings.pathAsString, MutLogFileSettings(logFileSettings))

  def removeLogFileSetting(pathAsString: String): Unit = {
    logFileSettingsProperty.remove(pathAsString)
  }

  def getStageSettings(): StageSettings = stageSettings.petrify()

  def set(settings: Settings) = {
    setStageSettings(settings.stageSettings)
    setLogFileSettings(settings.logFileSettings)
    setSomeActive(settings.someActive)
  }

  def setSomeActive(path: Option[String]): Unit = someActiveLogProperty.set(path)

  def getSomeActive(): Option[String] = someActiveLogProperty.get()

  def setLogFileSettings(logFileSettings: Map[String, LogFileSettings]): Unit = {
    val m = for ((k, v) <- logFileSettings) yield {
      k -> MutLogFileSettings(v)
    }
    logFileSettingsProperty.putAll(m.asJava)
  }


  def petrify(): Settings = {
    val m = (for ((k, v) <- logFileSettingsProperty.get.asScala) yield k -> v.petrify()).toMap
    Settings(stageSettings.petrify(), m, getSomeActive())
  }

  def setStageSettings(stageSettings: StageSettings): Unit = {
    this.stageSettings.setX(stageSettings.x)
    this.stageSettings.setY(stageSettings.y)
    this.stageSettings.setHeight(stageSettings.height)
    this.stageSettings.setWidth(stageSettings.width)
  }
}