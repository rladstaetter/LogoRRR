package app.logorrr.conf.mut

import app.logorrr.conf.*
import app.logorrr.util.JetbrainsMonoFontStyleBinding
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.{ObjectBinding, StringBinding}
import javafx.beans.property.*
import javafx.collections.{FXCollections, ObservableList}

import java.util.function.Predicate
import scala.jdk.CollectionConverters.*

object MutLogFileSettings:

  def apply(logFileSettings: LogFileSettings): MutLogFileSettings =
    val s = new MutLogFileSettings
    s.fileIdProperty.set(logFileSettings.fileId)
    s.selectedLineNumberProperty.set(logFileSettings.selectedLineNumber)
    s.fontSizeProperty.set(logFileSettings.fontSize)
    s.blockSizeProperty.set(logFileSettings.blockSize)
    s.firstOpenedProperty.set(logFileSettings.firstOpened)
    s.dividerPositionProperty.set(logFileSettings.dividerPosition)
    s.setMutableSearchTerms(logFileSettings.searchTerms.map(f => MutableSearchTerm(f)))
    s.autoScrollActiveProperty.set(logFileSettings.autoScroll)
    s.lowerBoundaryProperty.set(logFileSettings.lowerTimestamp)
    s.upperBoundaryProperty.set(logFileSettings.upperTimestamp)
    logFileSettings.someTimeSettings.foreach(s.mutTimeSettings.set)
    s


class MutLogFileSettings:

  /** with the extractor the changelistener also fires if an element is changed */
  val autoScrollActiveProperty = new SimpleBooleanProperty(this, "autoScrollActiveProperty")
  val blockSizeProperty = new SimpleIntegerProperty(this, "blockSizeProperty")
  val dividerPositionProperty = new SimpleDoubleProperty(this, "dividerProperty")
  val fileIdProperty = new SimpleObjectProperty[FileId](this, "fileIdProperty")
  val firstOpenedProperty = new SimpleLongProperty(this, "firstOpenedProperty")
  val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty(this, "firstVisibleTextCellIndexProperty")
  val fontSizeProperty = new SimpleIntegerProperty(this, "fontSizeProperty")
  val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty(this, "lastVisibleTextCellIndexProperty")
  val mutSearchTerms: ObservableList[MutableSearchTerm] = FXCollections.observableArrayList[MutableSearchTerm](MutableSearchTerm.extractor)
  val selectedLineNumberProperty = new SimpleIntegerProperty(this, "selectedLineNumberProperty")
  val lowerBoundaryProperty = new SimpleLongProperty(this, "lowerBoundaryProperty")
  val upperBoundaryProperty = new SimpleLongProperty(this, "upperBoundaryProperty")
  val mutTimeSettings = new MutTimeSettings

  /** is reset on various user interface action which should trigger a configuration save action */
  val repaintProperty = new SimpleBooleanProperty(this, "repaintProperty", false)

  val activeSearchTermsBinding: ObjectBinding[Set[SearchTerm]] = new ObjectBinding[Set[SearchTerm]] {

    bind(mutSearchTerms)

    override def computeValue(): Set[SearchTerm] = mutSearchTerms.filtered((t: MutableSearchTerm) => t.isActive).asScala.map(_.asSearchTerm).toSet
  }

  val showPredicate = new LogFilePredicate(mutSearchTerms, lowerBoundaryProperty, upperBoundaryProperty)

  def setMutableSearchTerms(mutableSearchTerms: Seq[MutableSearchTerm]): Unit =
    mutSearchTerms.setAll(mutableSearchTerms *)

  def setFirstVisibleTextCellIndex(value: Int): Unit = firstVisibleTextCellIndexProperty.set(value)

  def setLastVisibleTextCellIndex(value: Int): Unit = lastVisibleTextCellIndexProperty.set(value)

  val fontStyleBinding: StringBinding = new JetbrainsMonoFontStyleBinding(fontSizeProperty)

  def setTimeSettings(timeSettings: TimeSettings): Unit = mutTimeSettings.set(timeSettings)

  def isAutoScrollActive: Boolean = autoScrollActiveProperty.get()

  def getFontSize: Int = fontSizeProperty.get()

  def getBlockSize: Int = blockSizeProperty.get()

  def getFileId: FileId = fileIdProperty.get()

  def getFirstOpened: Long = firstOpenedProperty.get()

  def mkImmutable(): LogFileSettings =
    LogFileSettings(fileIdProperty.get()
      , selectedLineNumberProperty.get()
      , firstOpenedProperty.get()
      , dividerPositionProperty.get()
      , fontSizeProperty.get()
      , getSearchTerms
      , blockSizeProperty.get()
      , if mutTimeSettings.validBinding.get() then Option(mutTimeSettings.mkImmutable()) else None
      , autoScrollActiveProperty.get()
      , firstVisibleTextCellIndexProperty.get()
      , lastVisibleTextCellIndexProperty.get()
      , lowerBoundaryProperty.get()
      , upperBoundaryProperty.get()
    )


  def getSearchTerms: Seq[SearchTerm] =
    mutSearchTerms.asScala.toSeq.map(f => SearchTerm(f.getValue, f.getColor, f.isActive))

  def allProps: Set[Property[?]] =
    Set(
      autoScrollActiveProperty
      , blockSizeProperty
      , dividerPositionProperty
      , fileIdProperty
      , firstOpenedProperty
      , firstVisibleTextCellIndexProperty
      , fontSizeProperty
      , lastVisibleTextCellIndexProperty
      , selectedLineNumberProperty
      , lowerBoundaryProperty
      , upperBoundaryProperty
      , repaintProperty
    ) ++ mutTimeSettings.allProps