package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.search.Filter
import javafx.beans.binding.{BooleanBinding, StringBinding}
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
    s.someLogEntrySettings.set(logFileSettings.someLogEntryInstantFormat)
    s.setAutoScroll(logFileSettings.autoScroll)
    s
  }
}

class MutLogFileSettings {

  private val pathAsStringProperty = new SimpleStringProperty()
  private val firstOpenedProperty = new SimpleLongProperty()
  val selectedIndexProperty = new SimpleIntegerProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val fontSizeProperty = new SimpleIntegerProperty()
  val autoScrollProperty = new SimpleBooleanProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  val someLogEntrySettings = new SimpleObjectProperty[Option[LogEntryInstantFormat]]()
  val blockWidthSettingsProperty = new SimpleIntegerProperty()

  val hasLogEntrySetting = new BooleanBinding {
    bind(someLogEntrySettings)

    override def computeValue(): Boolean = {
      Option(someLogEntrySettings.get()).exists(_.isDefined)
    }
  }

  val fontStyle: ObservableValue[_ <: String] = new StringBinding {
    bind(fontSizeProperty)

    override def computeValue(): String = LogoRRRFonts.jetBrainsMono(fontSizeProperty.get())
  }

  def setAutoScroll(autoScroll: Boolean): Unit = autoScrollProperty.set(autoScroll)

  def isAutoScroll(): Boolean = autoScrollProperty.get()

  def getFontSize(): Int = fontSizeProperty.get()

  def getFilters() = filtersProperty.asScala.toSeq

  def setBlockSettings(bs: BlockSettings): Unit = blockWidthSettingsProperty.set(bs.width)

  def setPathAsString(path: String): Unit = pathAsStringProperty.set(path)

  def getPathAsString(): String = pathAsStringProperty.get()

  def setSelectedIndex(index: Int): Unit = selectedIndexProperty.set(index)

  def setFontSize(fontSize: Int): Unit = fontSizeProperty.set(fontSize)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getDividerPosition(): Double = dividerPositionProperty.get()

  def getFirstOpened(): Long = firstOpenedProperty.get()

  def petrify(): LogFileSettings = {
    val lfs =
      LogFileSettings(pathAsStringProperty.get()
        , selectedIndexProperty.get()
        , firstOpenedProperty.get()
        , dividerPositionProperty.get()
        , fontSizeProperty.get()
        , filtersProperty.get().asScala.toSeq
        , BlockSettings(blockWidthSettingsProperty.get())
        , someLogEntrySettings.get()
        , autoScrollProperty.get())
    lfs
  }
}

