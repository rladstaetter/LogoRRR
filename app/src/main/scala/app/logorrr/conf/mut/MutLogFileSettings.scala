package app.logorrr.conf.mut

import app.logorrr.conf.*
import app.logorrr.model.LogEntry
import app.logorrr.util.JetbrainsMonoFontStyleBinding
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.ASearchTermToggleButton
import javafx.beans.binding.{BooleanBinding, StringBinding}
import javafx.beans.property.*
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ObservableList}

import java.time.format.DateTimeFormatter
import java.util.function.Predicate
import scala.jdk.CollectionConverters.*

object MutLogFileSettings:

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings =
    val s = new MutLogFileSettings
    s.setFileId(logFileSettings.fileId)
    s.setSelectedLineNumber(logFileSettings.selectedLineNumber)
    s.setFontSize(logFileSettings.fontSize)
    s.setBlockSettings(logFileSettings.blockSettings)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.setDividerPosition(logFileSettings.dividerPosition)
    s.setMutableSearchTerms(logFileSettings.searchTerms.map(f => MutableSearchTerm(f)))
    s.someTimestampSettings.set(logFileSettings.someTimestampSettings)
    logFileSettings.someTimestampSettings match
      case Some(sts) => s.setDateTimeFormatter(sts.dateTimeFormatter)
      case None =>
    s.setAutoScroll(logFileSettings.autoScroll)
    s.setLowerTimestampValue(logFileSettings.lowerTimestamp)
    s.setUpperTimestampValue(logFileSettings.upperTimestamp)
    s


class MutLogFileSettings:

  /** with the extractor the changelistener also fires if an element is changed */
  val mutSearchTerms: ObservableList[MutableSearchTerm] = FXCollections.observableArrayList[MutableSearchTerm](MutableSearchTerm.extractor)

  val showPredicate = new LogFilePredicate(mutSearchTerms)
  var someUnclassifiedSearchTerm: Option[ASearchTermToggleButton] = None

  val fileIdProperty = new SimpleObjectProperty[FileId]()
  private val firstOpenedProperty = new SimpleLongProperty()
  val dateTimeFormatterProperty = new SimpleObjectProperty[DateTimeFormatter](TimestampSettings.DefaultFormatter)

  val fontSizeProperty = new SimpleIntegerProperty()
  val blockSizeProperty = new SimpleIntegerProperty()
  val selectedLineNumberProperty = new SimpleIntegerProperty()
  val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty()

  private val someTimestampSettings = new SimpleObjectProperty[Option[TimestampSettings]](None)

  val dividerPositionProperty = new SimpleDoubleProperty()
  val autoScrollActiveProperty = new SimpleBooleanProperty()


  def setLowerTimestampValue(lowerValue: Long): Unit = showPredicate.lowerTimestampValueProperty.set(lowerValue)

  def getLowerTimestampValue: Long = showPredicate.lowerTimestampValueProperty.get()

  def setUpperTimestampValue(upperValue: Long): Unit = showPredicate.upperTimestampValueProperty.set(upperValue)

  def getUpperTimestampValue: Long = showPredicate.upperTimestampValueProperty.get()

  def getSomeTimestampSettings: Option[TimestampSettings] = someTimestampSettings.get()

  def getDateTimeFormatter: DateTimeFormatter = dateTimeFormatterProperty.get()

  def setDateTimeFormatter(dateTimeFormatter: DateTimeFormatter): Unit = dateTimeFormatterProperty.set(dateTimeFormatter)

  def setMutableSearchTerms(mutableSearchTerms: Seq[MutableSearchTerm]): Unit =
    mutSearchTerms.setAll(mutableSearchTerms *)


  val hasTimestampSetting: BooleanBinding = new BooleanBinding:
    bind(someTimestampSettings)

    override def computeValue(): Boolean =
      Option(someTimestampSettings.get()).exists(_.isDefined)

  def setFirstVisibleTextCellIndex(value: Int): Unit = firstVisibleTextCellIndexProperty.set(value)

  def setLastVisibleTextCellIndex(value: Int): Unit = lastVisibleTextCellIndexProperty.set(value)

  val fontStyleBinding: StringBinding = new JetbrainsMonoFontStyleBinding(fontSizeProperty)

  def setSomeTimestampSettings(timestampSettings: Option[TimestampSettings]): Unit =
    someTimestampSettings.set(timestampSettings)
    timestampSettings match
      case Some(value) => setDateTimeFormatter(value.dateTimeFormatter)
      case None => setDateTimeFormatter(null)

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

  def mkImmutable(): LogFileSettings =
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
      , showPredicate.lowerTimestampValueProperty.get()
      , showPredicate.upperTimestampValueProperty.get()
    )

  def getSearchTerms: Seq[SearchTerm] =
    mutSearchTerms.asScala.toSeq.map(f => SearchTerm(f.getValue, f.getColor, f.isActive))

