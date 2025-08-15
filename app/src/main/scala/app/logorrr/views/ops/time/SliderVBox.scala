package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAndPosAware}
import javafx.geometry.Pos
import javafx.scene.layout.VBox

object SliderVBox extends UiNodeFileIdAndPosAware {

  def uiNode(id: FileId, pos: Pos): UiNode = UiNode(id, pos, classOf[SliderVBox])

}

class SliderVBox(mutLogFileSettings: MutLogFileSettings
                 , pos: Pos
                 , tooltipText: String
                 , sliderRange : TimeRange) extends VBox {

  setId(SliderVBox.uiNode(mutLogFileSettings.getFileId, pos).value)
  val slider = new TimerSlider(mutLogFileSettings, pos, tooltipText, sliderRange)
  val label = new TimestampSliderLabel(mutLogFileSettings, slider)

  setAlignment(pos)
  getChildren.addAll(slider, label)

  def resetRange(range : TimeRange) : Unit = {
    slider.setRange(range)
  }
}
