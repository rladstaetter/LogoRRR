package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{LogEntry, UpdateLogFilePredicate}
import app.logorrr.util.TimeUtil
import app.logorrr.views.logfiletab.LogFilePane
import app.logorrr.views.ops.time.{SliderVBox, TimestampSettingsButton}
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.stage.Window
import javafx.util.Subscription

import scala.compiletime.uninitialized
import scala.language.postfixOps


class TimestampSettingsRegion(logFilePane: LogFilePane
                              , mutLogFileSettings: MutLogFileSettings
                              , chunkListView: ChunkListView[LogEntry]
                              , logEntries: ObservableList[LogEntry]):

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(logFilePane, mutLogFileSettings, chunkListView, logEntries, this)

  private lazy val lowerSliderVBox = new SliderVBox(Pos.CENTER_LEFT, "Configure earliest timestamp to be displayed")
  private lazy val upperSliderVBox = new SliderVBox(Pos.CENTER_RIGHT, "Configure latest timestamp to be displayed")

  private lazy val lowerSlider = lowerSliderVBox.slider
  private lazy val upperSlider = upperSliderVBox.slider

  var timeRangeSubscription: Subscription = uninitialized

  private var lowerSliderSub: Subscription = uninitialized
  private var upperSliderSub: Subscription = uninitialized

  // replace with property
  def initializeRanges(min: Long, max: Long): Unit =
    lowerSlider.setMin(min.toDouble)
    lowerSlider.setMax(max.toDouble)
    upperSlider.setMin(min.toDouble)
    upperSlider.setMax(max.toDouble)


  def setSliderPositions(lower: Long, upper: Long): Unit =
    lowerSliderVBox.setInstant(lower)
    upperSliderVBox.setInstant(upper)

  private def updateLowerTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue > upperSlider.getValue then lowerSlider.setValue(upperSlider.getValue)
    logFilePane.fireEvent(UpdateLogFilePredicate())

  private def updateUpperTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue < lowerSlider.getValue then upperSlider.setValue(lowerSlider.getValue)
    logFilePane.fireEvent(UpdateLogFilePredicate())

  val items: Seq[Node] = Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox)

  def init(owner: Window): Unit =
    timeRangeSubscription = logEntries.subscribe(() => {
      val (min, max) = TimeUtil.calcRange(logEntries)
      initializeRanges(min, max)
    })

    lowerSliderVBox.init(
      mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.mutTimeSettings.validBinding
      , mutLogFileSettings.mutTimeSettings.dateTimeFormatterBinding)

    upperSliderVBox.init(
      mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.mutTimeSettings.validBinding
      , mutLogFileSettings.mutTimeSettings.dateTimeFormatterBinding)

    // only set this if we start with timestamp settings enabled for performance reasons
    if mutLogFileSettings.mutTimeSettings.validBinding.get() then
      val (min, max) = TimeUtil.calcRange(logEntries)
      initializeRanges(min, max)
      setSliderPositions(mutLogFileSettings.lowerBoundaryProperty.get(), mutLogFileSettings.upperBoundaryProperty.get())

    mutLogFileSettings.lowerBoundaryProperty.bind(lowerSlider.valueProperty())
    mutLogFileSettings.upperBoundaryProperty.bind(upperSlider.valueProperty())


    lowerSliderSub = lowerSlider.valueProperty().subscribe((_, newVal) => updateLowerTimestampSlider(newVal))
    upperSliderSub = upperSlider.valueProperty().subscribe((_, newVal) => updateUpperTimestampSlider(newVal))

    timestampSettingsButton.init(owner
      , mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.mutTimeSettings.validBinding
    )


  def shutdown(): Unit =
    mutLogFileSettings.lowerBoundaryProperty.unbind()
    mutLogFileSettings.upperBoundaryProperty.unbind()
    lowerSliderSub.unsubscribe()
    upperSliderSub.unsubscribe()
    timeRangeSubscription.unsubscribe()
    lowerSliderVBox.shutdown()
    upperSliderVBox.shutdown()
    timestampSettingsButton.unbind()
