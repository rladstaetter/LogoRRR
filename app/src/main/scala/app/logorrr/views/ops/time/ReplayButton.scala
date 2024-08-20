package app.logorrr.views.ops.time

import app.logorrr.model.LogEntry
import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

import scala.collection.mutable.ListBuffer


class ReplayButton(filteredList: ObservableList[LogEntry]
                   , lowerSlider: TimerSlider
                   , upperSlider: TimerSlider) extends Button {

  var timeline: Timeline = _

  // since timerbutton is a stackpane, this css commands are necessary to have the same effect as
  // defined in primer-light.css
  setGraphic(new FontIcon(FontAwesomeRegular.PLAY_CIRCLE))
  setTooltip(new Tooltip("replay log"))
  setOnAction(_ => {
    lowerSlider.setValue(lowerSlider.getMin)
    upperSlider.setValue(upperSlider.getMin)
    Option(timeline) match {
      case Some(tl) => tl.stop()
      case None =>
    }
    val frames = mkFrames()
    timeline = new Timeline(frames.toList: _*)
    timeline.setCycleCount(Animation.INDEFINITE)
    timeline.play()
  })

  def stopTimer(): Unit = Option(timeline).foreach(_.stop())
  def mkFrames(): ListBuffer[KeyFrame] = {
    val keyFrames = new ListBuffer[KeyFrame]
    filteredList.forEach((t: LogEntry) => {
      t.someJfxDuration match {
        case Some(duration) =>
          val kf = new KeyFrame(duration, (_: ActionEvent) => {
            if (upperSlider.getValue < upperSlider.getMax) {
              upperSlider.setValue(upperSlider.getMin + duration.toMillis)
            }
          })
          keyFrames.addOne(kf)
        case None =>
      }
    })
    keyFrames
  }
}

