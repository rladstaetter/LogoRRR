package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.views.Filter
import javafx.beans.property._
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

  private val pathAsStringProperty = new SimpleStringProperty()
  private val firstOpenedProperty = new SimpleLongProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  val someLogEntrySettings = new SimpleObjectProperty[Option[LogEntryInstantFormat]]()
  val blockWidthSettingsProperty = new SimpleIntegerProperty()

  def setBlockSettings(bs: BlockSettings): Unit = blockWidthSettingsProperty.set(bs.width)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getFirstOpened(): Long = firstOpenedProperty.get()

  override def petrify(): LogFileSettings = LogFileSettings(pathAsStringProperty.get()
    , firstOpenedProperty.get()
    , dividerPositionProperty.get()
    , filtersProperty.get().asScala.toSeq
    , BlockSettings(blockWidthSettingsProperty.get())
    , someLogEntrySettings.get())
}

