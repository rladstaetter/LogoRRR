package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
import app.logorrr.model.{LogEntry, LogFileSettings, TimestampSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views
import app.logorrr.views.{Filter, MutFilter}
import app.logorrr.views.search.FilterButton
import app.logorrr.views.search.filter.AnyFilter
import app.logorrr.views.search.predicates.ContainsPredicate
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
    s.setFilters(logFileSettings.filters.map(f => views.MutFilter[String](ContainsPredicate(f.pattern), f.color, f.active)))
    s.someTimestampSettings.set(logFileSettings.someTimestampSettings)
    logFileSettings.someTimestampSettings match {
      case Some(sts) => s.setDateTimeFormatter(sts.dateTimeFormatter)
      case None =>
    }
    s.setAutoScroll(logFileSettings.autoScroll)
    // TODO: set values and boundaries correctly for sliders
    // https://github.com/rladstaetter/LogoRRR/issues/261
    //s.setLowerTimestampValue(logFileSettings.lowerTimestamp)
    //s.setUpperTimestampValue(logFileSettings.upperTimestamp)
    s
  }
}


class MutLogFileSettings {

  var someUnclassifiedFilter: Option[(MutFilter[_], FilterButton)] = None
  var filterButtons: Map[MutFilter[_], FilterButton] = Map[MutFilter[_], FilterButton]()

  /**
   * Filters are only active if selected.
   *
   * UnclassifiedFilter gets an extra handling since it depends on other filters
   *
   * @return
   */
  def computeCurrentFilter(): MutFilter[_] = {
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
          getLowerTimestampValue <= asMilli && asMilli <= getHigherTimestampValue
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
  private val lowerTimestampValueProperty = new SimpleLongProperty(LogFileSettings.DefaultLowerTimestamp)
  private val upperTimestampValueProperty = new SimpleLongProperty(LogFileSettings.DefaultUpperTimestamp)

  def setLowerTimestampValue(lowerValue: Long): Unit = lowerTimestampValueProperty.set(lowerValue)

  def getLowerTimestampValue: Long = lowerTimestampValueProperty.get()

  def setUpperTimestampValue(upperValue: Long): Unit = upperTimestampValueProperty.set(upperValue)

  def getHigherTimestampValue: Long = upperTimestampValueProperty.get()

  val dividerPositionProperty = new SimpleDoubleProperty()
  val autoScrollActiveProperty = new SimpleBooleanProperty()
  val filtersProperty: SimpleListProperty[MutFilter[_]] = new SimpleListProperty[MutFilter[_]](FXCollections.observableArrayList())

  def getSomeTimestampSettings: Option[TimestampSettings] = someTimestampSettings.get()

  def getDateTimeFormatter: DateTimeFormatter = dateTimeFormatterProperty.get()

  def setDateTimeFormatter(dateTimeFormatter: DateTimeFormatter): Unit = dateTimeFormatterProperty.set(dateTimeFormatter)

  def setFilters(filters: Seq[MutFilter[_]]): Unit = {
    filtersProperty.setAll(filters.asJava)
  }

  def getFilters: ObservableList[MutFilter[_]] = filtersProperty.get()


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
        , getFilters.asScala.toSeq.map(f => Filter(f.getPredicate.description, f.getColor, f.isActive))
        , BlockSettings(blockSizeProperty.get())
        , someTimestampSettings.get()
        , autoScrollActiveProperty.get()
        , firstVisibleTextCellIndexProperty.get()
        , lastVisibleTextCellIndexProperty.get()
        , lowerTimestampValueProperty.get()
        , upperTimestampValueProperty.get())
    lfs
  }
}

