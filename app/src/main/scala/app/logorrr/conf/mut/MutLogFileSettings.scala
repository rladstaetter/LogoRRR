package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.views.{Filter, Fltr}
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleLongProperty, SimpleMapProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.collections.FXCollections

import scala.jdk.CollectionConverters._

object MutLogFileSettings {

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings = {
    val s = new MutLogFileSettings
    s.blockWidthSettingsProperty.set(logFileSettings.blockSettings.width)
    s.pathAsStringProperty.set(logFileSettings.pathAsString)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.dividerPositionProperty.set(logFileSettings.dividerPosition)
    s.filtersProperty.setAll(logFileSettings.filters.asJava)
    s.someLogEntrySettings.set(logFileSettings.someLogEntrySetting)
    s
  }
}

class MutLogFileSettings extends Petrify[LogFileSettings] {

  def setBlockSettings(bs: BlockSettings): Unit = blockWidthSettingsProperty.set(bs.width)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getFirstOpened() : Long = firstOpenedProperty.get()

  private val pathAsStringProperty = new SimpleStringProperty()
  private val firstOpenedProperty = new SimpleLongProperty()
  private val dividerPositionProperty = new SimpleDoubleProperty()
  private val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  private val someLogEntrySettings = new SimpleObjectProperty[Option[LogEntryInstantFormat]]()
  private val blockWidthSettingsProperty = new SimpleIntegerProperty()

  override def petrify(): LogFileSettings = LogFileSettings(pathAsStringProperty.get()
    , firstOpenedProperty.get()
    , dividerPositionProperty.get()
    , filtersProperty.get().asScala.toSeq
    , BlockSettings(blockWidthSettingsProperty.get())
    , someLogEntrySettings.get())
}

