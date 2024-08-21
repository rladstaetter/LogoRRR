package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import javafx.geometry.Pos
import javafx.scene.layout.VBox

class SliderVBox(mutLogFileSettings: MutLogFileSettings
                 , tooltipText: String
                 , position: Pos) extends VBox {

  val slider = new TimerSlider(mutLogFileSettings.hasTimestampSetting, tooltipText)
  val label = new TimestampSliderLabel(mutLogFileSettings, slider)

  setAlignment(position)
  getChildren.addAll(slider, label)
}
