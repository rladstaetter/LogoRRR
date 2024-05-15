package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
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
    s.setFileId(logFileSettings.fileId)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.setFilters(logFileSettings.filters)
    s.someLogEntrySettingsProperty.set(logFileSettings.someLogEntryInstantFormat)
    s.setAutoScroll(logFileSettings.autoScroll)
    s
  }
}


class MutLogFileSettings {

  private val fileIdProperty = new SimpleObjectProperty[FileId]()
  private val firstOpenedProperty = new SimpleLongProperty()
  val selectedLineNumberProperty = new SimpleIntegerProperty()
  val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty()
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

  def setFirstVisibleTextCellIndex(value: Int): Unit = firstVisibleTextCellIndexProperty.set(value)

  def setLastVisibleTextCellIndex(value: Int): Unit = lastVisibleTextCellIndexProperty.set(value)

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

  def getBlockSize: Int = blockSizeProperty.get()

  def setFileId(path: FileId): Unit = fileIdProperty.set(path)

  def getFileId: FileId = fileIdProperty.get()

  def setSelectedLineNumber(lineNumber: Int): Unit = selectedLineNumberProperty.set(lineNumber)

  def setFontSize(fontSize: Int): Unit = fontSizeProperty.set(fontSize)

  def setDividerPosition(dividerPosition: Double): Unit = dividerPositionProperty.set(dividerPosition)

  def getDividerPosition: Double = dividerPositionProperty.get()

  def getFirstOpened: Long = firstOpenedProperty.get()

  def petrify(): LogFileSettings = {
    val lfs =
      LogFileSettings(fileIdProperty.get()
        , selectedLineNumberProperty.get()
        , firstOpenedProperty.get()
        , dividerPositionProperty.get()
        , fontSizeProperty.get()
        , filtersProperty.get().asScala.toSeq
        , BlockSettings(blockSizeProperty.get())
        , someLogEntrySettingsProperty.get()
        , autoScrollActiveProperty.get()
        , firstVisibleTextCellIndexProperty.get()
        , lastVisibleTextCellIndexProperty.get())
    lfs
  }
}

