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
                 , tooltipText: String
                 , pos: Pos) extends VBox {

  setId(SliderVBox.uiNode(mutLogFileSettings.getFileId,pos).value)
  val slider = new TimerSlider(mutLogFileSettings.hasTimestampSetting, tooltipText)
  val label = new TimestampSliderLabel(mutLogFileSettings, slider)

  setAlignment(pos)
  getChildren.addAll(slider, label)
}
