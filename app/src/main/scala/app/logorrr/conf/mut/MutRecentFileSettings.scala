package app.logorrr.conf.mut

import app.logorrr.conf.RecentFileSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.views.{Filter, Fltr}
import javafx.beans.property.{SimpleDoubleProperty, SimpleListProperty, SimpleMapProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.collections.FXCollections

import scala.jdk.CollectionConverters._

object MutLogFileSettings {

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings = {
    val s = new MutLogFileSettings
    s.pathAsStringProperty.set(logFileSettings.pathAsString)
    s.dividerPositionProperty.set(logFileSettings.dividerPosition)
    s.filtersProperty.setAll(logFileSettings.filters.asJava)
    s.someLogEntrySettings.set(logFileSettings.someLogEntrySetting)
    s
  }
}

class MutLogFileSettings extends Petrify[LogFileSettings] {

  val pathAsStringProperty = new SimpleStringProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  val someLogEntrySettings = new SimpleObjectProperty[Option[LogEntryInstantFormat]]()

  override def petrify(): LogFileSettings = LogFileSettings(pathAsStringProperty.get()
    , dividerPositionProperty.get()
    , filtersProperty.get().asScala.toSeq
    , someLogEntrySettings.get())
}

class MutRecentFileSettings extends Petrify[RecentFileSettings] {

  val logFileDefinitionsProperty = new SimpleMapProperty[String, MutLogFileSettings]()
  val someActiveLogReportProperty = new SimpleObjectProperty[Option[String]](None)

  def setLogFileDefinitions(logFileDefinitions: Map[String, LogFileSettings]): Unit = {
    val m = for ((k, v) <- logFileDefinitions) yield {
      k -> MutLogFileSettings(v)
    }
    logFileDefinitionsProperty.set(FXCollections.observableMap(m.asJava))
  }

  def setSomeActiveLogReport(someActiveLogReport: Option[String]): Unit = someActiveLogReportProperty.set(someActiveLogReport)

  override def petrify(): RecentFileSettings = {
    val m = (for ((k, v) <- logFileDefinitionsProperty.get.asScala) yield k -> v.petrify()).toMap
    RecentFileSettings(m, someActiveLogReportProperty.get)
  }
}
