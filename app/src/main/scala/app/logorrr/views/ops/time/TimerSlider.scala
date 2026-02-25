package app.logorrr.views.ops.time

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAndPosAware}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.geometry.Pos
import javafx.scene.control.{Slider, Tooltip}
import javafx.util.Subscription

import scala.compiletime.uninitialized


object TimerSlider extends UiNodeFileIdAndPosAware:

  val Width = 350

  override def uiNode(id: FileId, pos: Pos): UiNode = UiNode(id, pos, classOf[TimerSlider])

class TimerSlider(pos: Pos, tooltipText: String)
  extends Slider with BoundId(TimerSlider.uiNode(_, pos).value):

  setPrefWidth(TimerSlider.Width)
  setTooltip(new Tooltip(tooltipText))

  def setInstant(instant: Long): Unit = setValue(instant.doubleValue())

  def setRange(range: TimeRange): Unit =
    setMin(range.min.toDouble)
    setMax(range.max.toDouble)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , validTimestampBinding: BooleanBinding): Unit =
    bindIdProperty(fileIdProperty)
    visibleProperty().bind(validTimestampBinding)
    disableProperty().bind(validTimestampBinding.not)

  def shutdown(): Unit =
    unbindIdProperty()
    visibleProperty().unbind()
    disableProperty().unbind()