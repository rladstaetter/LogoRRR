package app.logorrr.views.ops.time

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundId, LogEntry}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAndPosAware}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.{Slider, Tooltip}
import javafx.util.Subscription

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import scala.compiletime.uninitialized


object TimerSlider extends UiNodeFileIdAndPosAware:

  val Width = 350
  override def uiNode(id: FileId, pos: Pos): UiNode = UiNode(id, pos, classOf[TimerSlider])

class TimerSlider(pos: Pos, tooltipText: String)
  extends Slider with BoundId(TimerSlider.uiNode(_, pos).value):

  setPrefWidth(TimerSlider.Width)
  setTooltip(new Tooltip(tooltipText))

  val timeRangeProperty = new SimpleObjectProperty[TimeRange]()
  var timeRangeSubscription: Subscription = uninitialized

  def setInstant(instant: Instant): Unit = setValue(instant.toEpochMilli.doubleValue())

  def setRange(range: TimeRange): Unit =
    setMin(range.start.toEpochMilli.doubleValue)
    setMax(range.end.toEpochMilli.doubleValue())

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , hasTimestampSetting: BooleanBinding
           , timeRangeProperty: ObjectPropertyBase[TimeRange]): Unit =
    bindIdProperty(fileIdProperty)
    timeRangeSubscription =
      this.timeRangeProperty.subscribe(r => {
        Option(r) match {
          case Some(value) => setRange(value)
          case None =>
        }

      })
    this.timeRangeProperty.bind(timeRangeProperty)
    visibleProperty().bind(hasTimestampSetting)
    disableProperty().bind(hasTimestampSetting.not)

  def shutdown(): Unit =
    timeRangeSubscription.unsubscribe()
    this.timeRangeProperty.unbind()
    unbindIdProperty()
    visibleProperty().unbind()
    disableProperty().unbind()