package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.search.Filter
import javafx.beans.binding.{BooleanBinding, StringBinding}
import javafx.beans.property._
import javafx.collections.FXCollections

import scala.jdk.CollectionConverters._

object MutLogFileSettings {

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings = {
    val s = new MutLogFileSettings
    s.setSelectedLineNumber(logFileSettings.selectedLineNumber)
    s.setFontSize(logFileSettings.fontSize)
    s.setBlockSettings(logFileSettings.blockSettings)
    s.setPathAsString(logFileSettings.pathAsString)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.setFilters(logFileSettings.filters)
    s.someLogEntrySettingsProperty.set(logFileSettings.someLogEntryInstantFormat)
    s.setAutoScroll(logFileSettings.autoScroll)
    s
  }
}

class MutLogFileSettings {

  private val pathAsStringProperty = new SimpleStringProperty()
  private val firstOpenedProperty = new SimpleLongProperty()
  val selectedLineNumberProperty = new SimpleIntegerProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val fontSizeProperty = new SimpleIntegerProperty()

  val autoScrollActiveProperty = new SimpleBooleanProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())
  val someLogEntrySettingsProperty = new SimpleObjectProperty[Option[LogEntryInstantFormat]](None)
  val blockSizeProperty = new SimpleIntegerProperty()

  def setFilters(filters: Seq[Filter]): Unit = {
    filtersProperty.setAll(filters.asJava)
  }

  val hasLogEntrySettingBinding: BooleanBinding = new BooleanBinding {
    bind(someLogEntrySettingsProperty)

    override def computeValue(): Boolean = {
      Option(someLogEntrySettingsProperty.get()).exists(_.isDefined)
    }
  }

  val fontStyleBinding: StringBinding = new StringBinding {
    bind(fontSizeProperty)

    override def computeValue(): String = LogoRRRFonts.jetBrainsMono(fontSizeProperty.get())
  }

  def setLogEntryInstantFormat(lef: LogEntryInstantFormat): Unit = {
    someLogEntrySettingsProperty.set(Option(lef))
  }

  def setAutoScroll(autoScroll: Boolean): Unit = autoScrollActiveProperty.set(autoScroll)

  def isAutoScrollActive: Boolean = autoScrollActiveProperty.get()

  def getFontSize: Int = fontSizeProperty.get()

  def setBlockSettings(bs: BlockSettings): Unit = blockSizeProperty.set(bs.size)

  def setPathAsString(path: String): Unit = pathAsStringProperty.set(path)

  def getPathAsString(): String = pathAsStringProperty.get()

  def setSelectedLineNumber(lineNumber: Int): Unit = selectedLineNumberProperty.set(lineNumber)

  def setFontSize(fontSize: Int): Unit = fontSizeProperty.set(fontSize)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getDividerPosition(): Double = dividerPositionProperty.get()

  def getFirstOpened(): Long = firstOpenedProperty.get()

  def petrify(): LogFileSettings = {
    val lfs =
      LogFileSettings(pathAsStringProperty.get()
        , selectedLineNumberProperty.get()
        , firstOpenedProperty.get()
        , dividerPositionProperty.get()
        , fontSizeProperty.get()
        , filtersProperty.get().asScala.toSeq
        , BlockSettings(blockSizeProperty.get())
        , someLogEntrySettingsProperty.get()
        , autoScrollActiveProperty.get())
    lfs
  }
}

