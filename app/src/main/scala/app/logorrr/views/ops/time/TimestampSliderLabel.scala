package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import javafx.beans.binding.Bindings
import javafx.scene.control.{Label, Slider}

class TimestampSliderLabel(mutLogFileSettings: MutLogFileSettings
                           , slider: Slider) extends Label {
  textProperty().bind(Bindings.createStringBinding(() => {
    TimerSlider.format(slider.getValue.longValue(), mutLogFileSettings.getDateTimeFormatter)
  }, slider.valueProperty))
  visibleProperty().bind(mutLogFileSettings.hasLogEntrySettingBinding)
}
