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
    s.setSelectedIndex(logFileSettings.selectedIndex)
    s.setBlockSettings(logFileSettings.blockSettings)
    s.pathAsStringProperty.set(logFileSettings.pathAsString)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.filtersProperty.setAll(logFileSettings.filters.asJava)
    s.someLogEntrySettings.set(logFileSettings.someLogEntrySetting)
    s
  }
}

class MutLogFileSettings extends Petrify[LogFileSettings] {

  val selectedIndexProperty = {
    val ip = new SimpleIntegerProperty()
    ip
  }

  def getSelectedIndex = selectedIndexProperty.get()

  private val pathAsStringProperty = new SimpleStringProperty()
  private val firstOpenedProperty = new SimpleLongProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  val someLogEntrySettings = new SimpleObjectProperty[Option[LogEntryInstantFormat]]()
  val blockWidthSettingsProperty = new SimpleIntegerProperty()

  def setBlockSettings(bs: BlockSettings): Unit = blockWidthSettingsProperty.set(bs.width)

  def setSelectedIndex(index: Int): Unit = selectedIndexProperty.set(index)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getFirstOpened(): Long = firstOpenedProperty.get()

  override def petrify(): LogFileSettings = LogFileSettings(pathAsStringProperty.get()
    , selectedIndexProperty.get()
    , firstOpenedProperty.get()
    , dividerPositionProperty.get()
    , filtersProperty.get().asScala.toSeq
    , BlockSettings(blockWidthSettingsProperty.get())
    , someLogEntrySettings.get())
}

