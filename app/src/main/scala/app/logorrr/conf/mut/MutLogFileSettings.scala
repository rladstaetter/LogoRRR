package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
import app.logorrr.model.{LogEntry, LogFileSettings, TimestampSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.search.{AnyFilter, Filter, FilterButton, Fltr}
import javafx.beans.binding.{BooleanBinding, StringBinding}
import javafx.beans.property._
import javafx.collections.transformation.FilteredList
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
    s.setLowerTimestamp(logFileSettings.lowerTimestamp)
    s.setUpperTimestamp(logFileSettings.upperTimestamp)
    s
  }
}


class MutLogFileSettings {

  var someUnclassifiedFilter: Option[(Filter, FilterButton)] = None
  var filterButtons: Map[Filter, FilterButton] = Map[Filter, FilterButton]()

  /**
   * Filters are only active if selected.
   *
   * UnclassifiedFilter gets an extra handling since it depends on other filters
   *
   * @return
   */
  def computeCurrentFilter(): Fltr = {
    new AnyFilter(someUnclassifiedFilter.map(fst => if (fst._2.isSelected) Set(fst._1) else Set()).getOrElse(Set()) ++
      filterButtons.filter(fst => fst._2.isSelected).keySet)
  }

  /**
   * Reduce current displayed log entries by applying text filters and consider also the time stamp range.
   *
   * @param filteredList list to filter
   */
  def updateActiveFilter(filteredList: FilteredList[LogEntry]): Unit = {
    filteredList.setPredicate((entry: LogEntry) =>
      (entry.someInstant match {
        case None => true // if instant is not set, return true
        case Some(value) =>
          val asMilli = value.toEpochMilli
          getLowTimestampBoundary <= asMilli && asMilli <= getHighTimestampBoundary
      }) && computeCurrentFilter().matches(entry.value))
  }


  private val fileIdProperty = new SimpleObjectProperty[FileId]()
  private val firstOpenedProperty = new SimpleLongProperty()
  private val someTimestampSettings = new SimpleObjectProperty[Option[TimestampSettings]](None)
  private val dateTimeFormatterProperty = new SimpleObjectProperty[DateTimeFormatter](TimestampSettings.DefaultFormatter)

  val fontSizeProperty = new SimpleIntegerProperty()
  val blockSizeProperty = new SimpleIntegerProperty()
  val selectedLineNumberProperty = new SimpleIntegerProperty()
  val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  private val lowerTimestampProperty = new SimpleLongProperty(LogFileSettings.DefaultLowerTimestamp)
  private val upperTimestampProperty = new SimpleLongProperty(LogFileSettings.DefaultUpperTimestamp)

  def setLowerTimestamp(lowerValue: Long): Unit = lowerTimestampProperty.set(lowerValue)

  def getLowTimestampBoundary: Long = lowerTimestampProperty.get()

  def setUpperTimestamp(upperValue: Long): Unit = upperTimestampProperty.set(upperValue)

  def getHighTimestampBoundary: Long = upperTimestampProperty.get()

  val dividerPositionProperty = new SimpleDoubleProperty()
  val autoScrollActiveProperty = new SimpleBooleanProperty()
  val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList())

  def getSomeTimestampSettings: Option[TimestampSettings] = someTimestampSettings.get()

  def getDateTimeFormatter: DateTimeFormatter = dateTimeFormatterProperty.get()

  def setDateTimeFormatter(dateTimeFormatter: DateTimeFormatter): Unit = dateTimeFormatterProperty.set(dateTimeFormatter)

  def setFilters(filters: Seq[Filter]): Unit = {
    filtersProperty.setAll(filters.asJava)
  }

  def getFilters: ObservableList[Filter] = filtersProperty.get()


  val hasTimestampSetting: BooleanBinding = new BooleanBinding {
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
        , getFilters.asScala.toSeq
        , BlockSettings(blockSizeProperty.get())
        , someTimestampSettings.get()
        , autoScrollActiveProperty.get()
        , firstVisibleTextCellIndexProperty.get()
        , lastVisibleTextCellIndexProperty.get()
        , lowerTimestampProperty.get()
        , upperTimestampProperty.get())
    lfs
  }
}

