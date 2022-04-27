package app.logorrr.conf.mut

import app.logorrr.conf.{BlockSettings}
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.views.{Filter, Fltr}
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleMapProperty, SimpleObjectProperty, SimpleStringProperty}
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
  val blockWidthSettingsProperty = new SimpleIntegerProperty()

  override def petrify(): LogFileSettings = LogFileSettings(pathAsStringProperty.get()
    , dividerPositionProperty.get()
    , filtersProperty.get().asScala.toSeq
    , BlockSettings(blockWidthSettingsProperty.get())
    , someLogEntrySettings.get())
}

