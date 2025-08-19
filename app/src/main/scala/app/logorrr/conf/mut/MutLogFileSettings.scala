package app.logorrr.conf.mut

import app.logorrr.clv.color.ColorMatcher
import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
import app.logorrr.model.{LogEntry, LogFileSettings, TimestampSettings}
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.ops.time.TimeRange
import app.logorrr.views.search.SearchTermButton
import app.logorrr.views.{MutableSearchTerm, SearchTerm}
import javafx.beans.binding.{BooleanBinding, ObjectBinding, StringBinding}
import javafx.beans.property._
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ObservableList}

import java.time.Instant
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._
import scala.util.Try

object MutLogFileSettings {

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings = {
    val s = new MutLogFileSettings
    s.setFileId(logFileSettings.fileId)
    s.setSelectedLineNumber(logFileSettings.selectedLineNumber)
    s.setFontSize(logFileSettings.fontSize)
    s.setBlockSettings(logFileSettings.blockSettings)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.setFilters(logFileSettings.searchTerms.map(f => MutableSearchTerm(f)))
    s.someTimestampSettings.set(logFileSettings.someTimestampSettings)
    logFileSettings.someTimestampSettings match {
      case Some(sts) => s.setDateTimeFormatter(sts.dateTimeFormatter)
      case None =>
    }
    s.setAutoScroll(logFileSettings.autoScroll)
    s.setLowerTimestampValue(logFileSettings.lowerTimestamp)
    s.setUpperTimestampValue(logFileSettings.upperTimestamp)
    s
  }
}


class MutLogFileSettings {

  var someUnclassifiedFilter: Option[(MutableSearchTerm, SearchTermButton)] = None
  var filterButtons: Map[ColorMatcher, SearchTermButton] = Map[ColorMatcher, SearchTermButton]()

  private val fileIdProperty = new SimpleObjectProperty[FileId]()
  private val firstOpenedProperty = new SimpleLongProperty()
  private val dateTimeFormatterProperty = new SimpleObjectProperty[DateTimeFormatter](TimestampSettings.DefaultFormatter)

  val fontSizeProperty = new SimpleIntegerProperty()
  val blockSizeProperty = new SimpleIntegerProperty()
  val selectedLineNumberProperty = new SimpleIntegerProperty()
  val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty()

  private val someTimestampSettings = new SimpleObjectProperty[Option[TimestampSettings]](None)
  private val lowerTimestampValueProperty = new SimpleLongProperty()
  private val upperTimestampValueProperty = new SimpleLongProperty()

  //  private val lowerTimestampValueProperty = new SimpleLongProperty(LogFileSettings.DefaultLowerTimestamp)
  //  private val upperTimestampValueProperty = new SimpleLongProperty(LogFileSettings.DefaultUpperTimestamp)

  val dividerPositionProperty = new SimpleDoubleProperty()
  val autoScrollActiveProperty = new SimpleBooleanProperty()
  val filtersProperty: SimpleListProperty[MutableSearchTerm] = new SimpleListProperty[MutableSearchTerm](FXCollections.observableArrayList())


  private def matchFilter(entry: LogEntry): Boolean = {
    val matchedFilter = SearchTerm.matches(entry.value, getSearchTerms.toSet)
    someUnclassifiedFilter match {
      case Some((_, b)) =>
        val dontMatch = SearchTerm.dontMatch(entry.value, getSearchTerms.toSet)
        if (b.isSelected) {
          val res = dontMatch || matchedFilter
          res
        } else {
          matchedFilter
        }
      case _ =>
        matchedFilter
    }
  }


  /**
   * Reduce current displayed log entries by applying text filters and consider also the time stamp range.
   *
   * @param filteredList list to filter
   */
  def updateActiveFilter(filteredList: FilteredList[LogEntry]): Unit = {
    filteredList.setPredicate((entry: LogEntry) =>
      matchTimeRange(entry) && matchFilter(entry))
  }


  private def matchTimeRange(entry: LogEntry): Boolean = {
    entry.someInstant match {
      case None => true // if instant is not set, return true
      case Some(value) =>
        val asMilli = value.toEpochMilli
        getLowerTimestampValue <= asMilli && asMilli <= getUpperTimestampValue
    }
  }

  def setLowerTimestampValue(lowerValue: Long): Unit = lowerTimestampValueProperty.set(lowerValue)

  def getLowerTimestampValue: Long = lowerTimestampValueProperty.get()

  val filteredRangeBinding: ObjectBinding[TimeRange] = new ObjectBinding[TimeRange]() {
    bind(lowerTimestampValueProperty, upperTimestampValueProperty)

    override def computeValue(): TimeRange = {
      (for {lower <- Option(lowerTimestampValueProperty.get()).map(Instant.ofEpochMilli)
            upper <- Option(upperTimestampValueProperty.get()).map(Instant.ofEpochMilli)
            } yield Try(TimeRange(lower, upper)).getOrElse(null)).orNull
    }
  }

  def setUpperTimestampValue(upperValue: Long): Unit = upperTimestampValueProperty.set(upperValue)

  def getUpperTimestampValue: Long = upperTimestampValueProperty.get()

  def getSomeTimestampSettings: Option[TimestampSettings] = someTimestampSettings.get()

  def getDateTimeFormatter: DateTimeFormatter = dateTimeFormatterProperty.get()

  def setDateTimeFormatter(dateTimeFormatter: DateTimeFormatter): Unit = dateTimeFormatterProperty.set(dateTimeFormatter)

  def setFilters(filters: Seq[MutableSearchTerm]): Unit = {
    filtersProperty.setAll(filters.asJava)
  }

  def getFilters: ObservableList[MutableSearchTerm] = filtersProperty.get()


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
        , getSearchTerms
        , BlockSettings(blockSizeProperty.get())
        , someTimestampSettings.get()
        , autoScrollActiveProperty.get()
        , firstVisibleTextCellIndexProperty.get()
        , lastVisibleTextCellIndexProperty.get()
        , lowerTimestampValueProperty.get()
        , upperTimestampValueProperty.get())
    lfs
  }

  def getSearchTerms: Seq[SearchTerm] = {
    getFilters.asScala.toSeq.map(f => SearchTerm(f.getPredicate.description, f.getColor, f.isActive))
  }
}

