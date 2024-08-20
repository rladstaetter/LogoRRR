package app.logorrr.views.ops.time

import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.event.ActionEvent
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.layout.StackPane
import javafx.util.Duration
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


class ReplayButton(lowerSlider: TimerSlider
                   , upperSlider: TimerSlider) extends StackPane {

lowerSlider == lowerSlider
  private def kf(increment:Double)  = new KeyFrame(Duration.seconds(1), (_: ActionEvent) => {
    if (upperSlider.getValue() < upperSlider.getMax()) {
      val value: Double = upperSlider.getValue() + increment
      println(value)
      upperSlider.setValue(value);
    }
  })

  // since timerbutton is a stackpane, this css commands are necessary to have the same effect as
  // defined in primer-light.css
  val button: Button = {
    val btn = new Button()
    btn.setBackground(null)
    btn.setStyle(
      """
        |-color-button-bg: -color-bg-subtle;
        |-fx-background-insets: 0;
        |""".stripMargin)
    btn.setGraphic(new FontIcon(FontAwesomeRegular.PLAY_CIRCLE))
    btn.setTooltip(new Tooltip("replay log"))
    btn.setOnAction(_ => {
      // lowerSlider.setValue(lowerSlider.getMin)
      // upperSlider.setValue(upperSlider.getMin)
      val increment = (upperSlider.getMax - upperSlider.getMin) / 60
      val timeline = new Timeline(kf(increment));
      timeline.setCycleCount(Animation.INDEFINITE)
      timeline.play()
    })
    btn
  }

  private val fontIcon = {
    val icon = new FontIcon()
    icon.setStyle("-fx-icon-code:fas-exclamation-circle;-fx-icon-color:rgba(255, 0, 0, 1);-fx-icon-size:8;")
    icon.setTranslateX(10)
    icon.setTranslateY(-10)

    /** red exclamation mark is only visible if there is no timestamp setting for a given log file */
    icon
  }

  getChildren.addAll(button, fontIcon)

}
