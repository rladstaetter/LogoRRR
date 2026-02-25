package app.logorrr.views.ops.time

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAndPosAware}
import javafx.beans.binding.{BooleanBinding, ObjectBinding}
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.geometry.Pos
import javafx.scene.layout.VBox

import java.time.format.DateTimeFormatter

object SliderVBox extends UiNodeFileIdAndPosAware:

  def uiNode(id: FileId, pos: Pos): UiNode = UiNode(id, pos, classOf[SliderVBox])


class SliderVBox(pos: Pos
                 , tooltipText: String) extends VBox
  with BoundId(SliderVBox.uiNode(_, pos).value):

  val slider = new TimerSlider(pos, tooltipText)
  val label = new TimestampSliderLabel

  setAlignment(pos)
  getChildren.addAll(slider, label)

  def setInstant(instant: Long): Unit = slider.setInstant(instant)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , validTimestampBinding: BooleanBinding
           , dateTimeFormatterProperty: ObjectBinding[DateTimeFormatter]): Unit =
    bindIdProperty(fileIdProperty)
    slider.init(fileIdProperty, validTimestampBinding)
    label.init(validTimestampBinding, slider.valueProperty(), dateTimeFormatterProperty)

  def shutdown(): Unit =
    unbindIdProperty()
    slider.shutdown()
    label.shutdown()