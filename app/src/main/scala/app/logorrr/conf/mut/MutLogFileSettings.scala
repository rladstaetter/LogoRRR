package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
import app.logorrr.model.{LogFileSettings, TimestampSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.search.Filter
import javafx.beans.binding.{BooleanBinding, StringBinding}
import javafx.beans.property._
import javafx.collections.{FXCollections, ObservableList}

import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._

object MutLogFileSettings {

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings = {
    val s = new MutLogFileSettings
    s.setFileId(logFileSettings.fileId)
    s.setSelectedLineNumber(logFileSettings.selectedLineNumber)
    s.setFontSize(logFileSettings.fontSize)
    s.setBlockSettings(logFileSettings.blockSettings)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.setFilters(logFileSettings.filters)
    s.someTimestampSettings.set(logFileSettings.someTimestampSettings)
    logFileSettings.someTimestampSettings match {
      case Some(sts) => s.setDateTimeFormatter(sts.dateTimeFormatter)
      case None =>
    }
    s.setAutoScroll(logFileSettings.autoScroll)
    s
  }
}


class MutLogFileSettings {

  private val fileIdProperty = new SimpleObjectProperty[FileId]()
  private val firstOpenedProperty = new SimpleLongProperty()
  private val someTimestampSettings = new SimpleObjectProperty[Option[TimestampSettings]](None)
  private val dateTimeFormatterProperty = new SimpleObjectProperty[DateTimeFormatter](TimestampSettings.Default.dateTimeFormatter)

  val fontSizeProperty = new SimpleIntegerProperty()
  val blockSizeProperty = new SimpleIntegerProperty()
  val selectedLineNumberProperty = new SimpleIntegerProperty()
  val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  val dividerPositionProperty = new SimpleDoubleProperty()
  val autoScrollActiveProperty = new SimpleBooleanProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())

  def getSomeTimestampSettings(): Option[TimestampSettings] = someTimestampSettings.get()

  def getDateTimeFormatter(): DateTimeFormatter = dateTimeFormatterProperty.get()

  def setDateTimeFormatter(dateTimeFormatter: DateTimeFormatter): Unit = dateTimeFormatterProperty.set(dateTimeFormatter)

  def setFilters(filters: Seq[Filter]): Unit = {
    filtersProperty.setAll(filters.asJava)
  }

  def getFilters(): ObservableList[Filter] = filtersProperty.get()


  val hasLogEntrySettingBinding: BooleanBinding = new BooleanBinding {
    bind(someTimestampSettings)

    override def computeValue(): Boolean = {
      Option(someTimestampSettings.get()).exists(_.isDefined)
    }
  }

  def setFirstVisibleTextCellIndex(value: Int): Unit = firstVisibleTextCellIndexProperty.set(value)

  def setLastVisibleTextCellIndex(value: Int): Unit = lastVisibleTextCellIndexProperty.set(value)

  val fontStyleBinding: StringBinding = new StringBinding {
    bind(fontSizeProperty)

    override def computeValue(): String = LogoRRRFonts.jetBrainsMono(fontSizeProperty.get())
  }

  def setSomeLogEntryInstantFormat(someLef: Option[TimestampSettings]): Unit = {
    someTimestampSettings.set(someLef)
    someLef match {
      case Some(value) => setDateTimeFormatter(value.dateTimeFormatter)
      case None => setDateTimeFormatter(null)
    }
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

  def mkImmutable(): LogFileSettings = {
    val lfs =
      LogFileSettings(fileIdProperty.get()
        , selectedLineNumberProperty.get()
        , firstOpenedProperty.get()
        , dividerPositionProperty.get()
        , fontSizeProperty.get()
        , getFilters().asScala.toSeq
        , BlockSettings(blockSizeProperty.get())
        , someTimestampSettings.get()
        , autoScrollActiveProperty.get()
        , firstVisibleTextCellIndexProperty.get()
        , lastVisibleTextCellIndexProperty.get())
    lfs
  }
}

