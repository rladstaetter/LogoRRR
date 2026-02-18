package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.time.{SliderVBox, TimeRange, TimeUtil, TimestampSettingsButton}
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.stage.Window
import javafx.util.Subscription

import java.util.function.Predicate
import scala.compiletime.uninitialized
import scala.language.postfixOps


class TimestampSettingsRegion(mutLogFileSettings: MutLogFileSettings
                              , chunkListView: ChunkListView[LogEntry]
                              , logEntries: ObservableList[LogEntry]
                              , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]):

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(mutLogFileSettings, chunkListView, logEntries, this)

  private lazy val lowerSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_LEFT, "Configure earliest timestamp to be displayed")
  private lazy val upperSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_RIGHT, "Configure latest timestamp to be displayed")

  private val lowerSlider = lowerSliderVBox.slider
  private val upperSlider = upperSliderVBox.slider

  val timeRangeProperty = new SimpleObjectProperty[TimeRange]()
  var timeRangeSubscription: Subscription = uninitialized
  var lowerSliderSub: Subscription = uninitialized
  var upperSliderSub: Subscription = uninitialized

  def initialTimeRange: TimeRange = TimeUtil.calcTimeInfo(logEntries).getOrElse(TimeRange.defaultTimeRange)

  // replace with property
  def initializeRanges(): Unit =
    val range = initialTimeRange
    lowerSlider.setRange(range)
    lowerSlider.setInstant(range.start)
    upperSlider.setRange(range)
    upperSlider.setInstant(range.end)
    setSliderPositions(range)


  private def setSliderPositions(filterRange: TimeRange): Unit =
    lowerSliderVBox.setInstant(filterRange.start)
    upperSliderVBox.setInstant(filterRange.end)

  private def updateLowerTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue > upperSlider.getValue then lowerSlider.setValue(upperSlider.getValue)
    mutLogFileSettings.setLowerTimestampValue(newValue.longValue())
    predicateProperty.set(null)
    predicateProperty.set(mutLogFileSettings.showPredicate)
  // mutLogFileSettings.updateActiveSearchTerms(filteredList)

  private def updateUpperTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue < lowerSlider.getValue then upperSlider.setValue(lowerSlider.getValue)
    mutLogFileSettings.setUpperTimestampValue(newValue.longValue())
    predicateProperty.set(null)
    predicateProperty.set(mutLogFileSettings.showPredicate)
  // mutLogFileSettings.updateActiveSearchTerms(filteredList)

  val items: Seq[Node] = Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox)

  def init(owner: Window): Unit =
    timeRangeSubscription =
      logEntries.subscribe(() => timeRangeProperty.set(TimeUtil.calcTimeInfo(logEntries).getOrElse(TimeRange.defaultTimeRange)))

    lowerSliderVBox.init(mutLogFileSettings.fileIdProperty, mutLogFileSettings.hasTimestampSetting, timeRangeProperty, mutLogFileSettings.dateTimeFormatterProperty)
    upperSliderVBox.init(mutLogFileSettings.fileIdProperty, mutLogFileSettings.hasTimestampSetting, timeRangeProperty, mutLogFileSettings.dateTimeFormatterProperty)

    lowerSliderSub =
      lowerSlider.valueProperty().subscribe((_, newVal) => updateLowerTimestampSlider(newVal))

    upperSliderSub =
      upperSlider.valueProperty().subscribe((_, newVal) => updateUpperTimestampSlider(newVal))

    timestampSettingsButton.init(
      mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.hasTimestampSetting
      , LogoRRRGlobals.getTimestampSettings.map(_.mkImmutable())
      , mutLogFileSettings.getSomeTimestampSettings
      , owner)

    timeRangeProperty.set(initialTimeRange)
    setSliderPositions(initialTimeRange)


  def shutdown(): Unit =
    lowerSliderSub.unsubscribe()
    upperSliderSub.unsubscribe()
    timeRangeSubscription.unsubscribe()
    lowerSliderVBox.shutdown()
    upperSliderVBox.shutdown()
    timestampSettingsButton.unbind()
