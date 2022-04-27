package app.logorrr.conf.mut

import app.logorrr.conf.LogoRRRGlobals.settings
import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.model.LogFileSettings
import javafx.beans.property.{SimpleListProperty, SimpleMapProperty, SimpleStringProperty}
import javafx.collections.FXCollections

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
    s.setLogFileOrdering(settings.logFileOrdering)
    s.setActive(settings.someActive.orNull)
    s
  }

}


class MutSettings {

  val stageSettings = new MutStageSettings
  val logFileDefinitionsProperty = new SimpleMapProperty[String, MutLogFileSettings]()
  val logFileOrderingsProperty = new SimpleListProperty[String](FXCollections.observableArrayList())
  val someActiveLogProperty = new SimpleStringProperty()

  def getStageSettings(): StageSettings = stageSettings.petrify()

  def set(settings: Settings) = {
    setStageSettings(settings.stageSettings)
    setLogFileSettings(settings.logFileSettings)
    setLogFileOrdering(settings.logFileOrdering)
    setActive(settings.someActive.orNull)
  }

  def setActive(path: String): Unit = someActiveLogProperty.set(path)

  def getActive(): Option[String] = Option(someActiveLogProperty.get())

  def setLogFileSettings(logFileDefinitions: Map[String, LogFileSettings]): Unit = {
    val m = for ((k, v) <- logFileDefinitions) yield {
      k -> MutLogFileSettings(v)
    }
    logFileDefinitionsProperty.set(FXCollections.observableMap(m.asJava))
  }

  def setLogFileOrdering(logFileOrdering: Seq[String]): Unit = logFileOrderingsProperty.set(FXCollections.observableArrayList(logFileOrdering: _*))

  def petrify(): Settings = {
    val m = (for ((k, v) <- logFileDefinitionsProperty.get.asScala) yield k -> v.petrify()).toMap
    Settings(stageSettings.petrify(), m, logFileOrderingsProperty.get().asScala.toSeq, getActive())
  }


  def setStageSettings(stageSettings: StageSettings): Unit = {
    this.stageSettings.setX(stageSettings.x)
    this.stageSettings.setY(stageSettings.y)
    this.stageSettings.setHeight(stageSettings.height)
    this.stageSettings.setWidth(stageSettings.width)
  }

}