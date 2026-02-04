package app.logorrr.views.ops.time

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAndPosAware}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.geometry.Pos
import javafx.scene.layout.VBox

import java.time.Instant
import java.time.format.DateTimeFormatter

object SliderVBox extends UiNodeFileIdAndPosAware:

  def uiNode(id: FileId, pos: Pos): UiNode = UiNode(id, pos, classOf[SliderVBox])


class SliderVBox(mutLogFileSettings: MutLogFileSettings
                 , pos: Pos
                 , tooltipText: String) extends VBox with BoundFileId(SliderVBox.uiNode(_, pos).value):

  val slider = new TimerSlider(pos, tooltipText)
  val label = new TimestampSliderLabel

  setAlignment(pos)
  getChildren.addAll(slider, label)

  def setInstant(instant: Instant): Unit = slider.setInstant(instant)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , hasTimestampBinding: BooleanBinding
           , timeRangeProperty: ObjectPropertyBase[TimeRange]
           , dateTimeFormatterProperty: SimpleObjectProperty[DateTimeFormatter]): Unit =
    bindIdProperty(fileIdProperty)
    slider.init(fileIdProperty, hasTimestampBinding, timeRangeProperty)
    label.init(hasTimestampBinding, slider.valueProperty(), dateTimeFormatterProperty)

  def shutdown(): Unit =
    unbindIdProperty()
    slider.shutdown()
    label.shutdown()