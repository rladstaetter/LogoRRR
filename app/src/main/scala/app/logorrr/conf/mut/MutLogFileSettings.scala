package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.Filter
import javafx.beans.binding.StringBinding
import javafx.beans.property._
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections

import scala.jdk.CollectionConverters._

object MutLogFileSettings {

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings = {
    val s = new MutLogFileSettings
    s.setSelectedIndex(logFileSettings.selectedIndex)
    s.setFontSize(logFileSettings.fontSize)
    s.setBlockSettings(logFileSettings.blockSettings)
    s.setPathAsString(logFileSettings.pathAsString)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.filtersProperty.setAll(logFileSettings.filters.asJava)
    s.someLogEntrySettings.set(logFileSettings.someLogEntrySetting)
    s
  }
}

class MutLogFileSettings extends Petrify[LogFileSettings] {
  def getFontSize(): Int = fontSizeProperty.get()

  def getFilters() = filtersProperty.asScala.toSeq


  private val pathAsStringProperty = new SimpleStringProperty()
  private val firstOpenedProperty = new SimpleLongProperty()
  val selectedIndexProperty = new SimpleIntegerProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val fontSizeProperty = new SimpleIntegerProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  val someLogEntrySettings = new SimpleObjectProperty[Option[LogEntryInstantFormat]]()
  val blockWidthSettingsProperty = new SimpleIntegerProperty()

  val fontStyle: ObservableValue[_ <: String] = new StringBinding {
    bind(fontSizeProperty)
    override def computeValue(): String = LogoRRRFonts.jetBrainsMono(fontSizeProperty.get())
  }

  def getSelectedIndex = selectedIndexProperty.get()

  def setBlockSettings(bs: BlockSettings): Unit = blockWidthSettingsProperty.set(bs.width)

  def setPathAsString(path: String): Unit = pathAsStringProperty.set(path)

  def setSelectedIndex(index: Int): Unit = selectedIndexProperty.set(index)

  def setFontSize(fontSize: Int): Unit = fontSizeProperty.set(fontSize)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getFirstOpened(): Long = firstOpenedProperty.get()

  override def petrify(): LogFileSettings = LogFileSettings(pathAsStringProperty.get()
    , selectedIndexProperty.get()
    , firstOpenedProperty.get()
    , dividerPositionProperty.get()
    , fontSizeProperty.get()
    , filtersProperty.get().asScala.toSeq
    , BlockSettings(blockWidthSettingsProperty.get())
    , someLogEntrySettings.get())
}

