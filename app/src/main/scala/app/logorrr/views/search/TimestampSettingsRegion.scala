package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.{LogFilePredicate, MutLogFileSettings}
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.time.{SliderVBox, TimeRange, TimeUtil, TimestampSettingsButton}
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.stage.Window
import javafx.util.Subscription

import java.util.function.Predicate
import scala.compiletime.uninitialized
import scala.language.postfixOps


class TimestampSettingsRegion(owner: Window
                              , mutLogFileSettings: MutLogFileSettings
                              , chunkListView: ChunkListView[LogEntry]
                              , logEntries: ObservableList[LogEntry]
                              , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]):

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(owner, mutLogFileSettings, chunkListView, logEntries, this)

  private lazy val lowerSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_LEFT, "Configure earliest timestamp to be displayed")
  private lazy val upperSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_RIGHT, "Configure latest timestamp to be displayed")

  private lazy val lowerSlider = lowerSliderVBox.slider
  private lazy val upperSlider = upperSliderVBox.slider

  val timeRangeProperty = new SimpleObjectProperty[TimeRange]()
  var timeRangeSubscription: Subscription = uninitialized
  var lowerSliderSub: Subscription = uninitialized
  var upperSliderSub: Subscription = uninitialized

  def calcRange: TimeRange = TimeUtil.calcTimeInfo(logEntries).getOrElse(TimeRange.defaultTimeRange)

  // replace with property
  def initializeRanges(): Unit =
    val range = calcRange
    lowerSlider.setRange(range)
    lowerSlider.setInstant(range.min)
    upperSlider.setRange(range)
    upperSlider.setInstant(range.max)
    setSliderPositions(range)


  private def setSliderPositions(filterRange: TimeRange): Unit =
    lowerSliderVBox.setInstant(filterRange.min)
    upperSliderVBox.setInstant(filterRange.max)

  private def updateLowerTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue > upperSlider.getValue then lowerSlider.setValue(upperSlider.getValue)
    mutLogFileSettings.setLowerTimestampValue(newValue.longValue())
    LogFilePredicate.update(predicateProperty, mutLogFileSettings.showPredicate)

  private def updateUpperTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue < lowerSlider.getValue then upperSlider.setValue(lowerSlider.getValue)
    mutLogFileSettings.setUpperTimestampValue(newValue.longValue())
    LogFilePredicate.update(predicateProperty, mutLogFileSettings.showPredicate)

  val items: Seq[Node] = Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox)

  def init(): Unit =
    timeRangeSubscription = logEntries.subscribe(() => timeRangeProperty.set(calcRange))

    lowerSliderVBox.init(mutLogFileSettings.fileIdProperty, mutLogFileSettings.hasTimestampSetting, timeRangeProperty, mutLogFileSettings.dateTimeFormatterProperty)
    upperSliderVBox.init(mutLogFileSettings.fileIdProperty, mutLogFileSettings.hasTimestampSetting, timeRangeProperty, mutLogFileSettings.dateTimeFormatterProperty)

    lowerSliderSub = lowerSlider.valueProperty().subscribe((_, newVal) => updateLowerTimestampSlider(newVal))
    upperSliderSub = upperSlider.valueProperty().subscribe((_, newVal) => updateUpperTimestampSlider(newVal))

    timestampSettingsButton.init(
      mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.hasTimestampSetting
    )

    // only set this if we start with timestamp settings enabled for performance reasons
    if mutLogFileSettings.getSomeTimestampSettings.isDefined then
      timeRangeProperty.set(calcRange)
      setSliderPositions(calcRange)


  def shutdown(): Unit =
    lowerSliderSub.unsubscribe()
    upperSliderSub.unsubscribe()
    timeRangeSubscription.unsubscribe()
    lowerSliderVBox.shutdown()
    upperSliderVBox.shutdown()
    timestampSettingsButton.unbind()
