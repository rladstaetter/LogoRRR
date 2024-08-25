package app.logorrr.views.ops

import javafx.animation.AnimationTimer
import javafx.scene.Node
import javafx.scene.control.{Labeled, Tooltip}

import java.time.Duration

class PulsatingAnimationTimer(labeled: Labeled
                              , iconLight: Node
                              , icon: Node
                              , tooltip: Tooltip
                              , tooltipText: String
                              , animationDuration: Duration) extends AnimationTimer {

  private var startTime: Long = -1
  var alpha: Double = 0.0
  var direction = true

  override def stop(): Unit = {
    super.stop()
    labeled.setGraphic(icon)
    labeled.setOpacity(1)
    tooltip.hide()
  }

  def handle(now: Long): Unit = {
    if (startTime < 0) {
      labeled.setGraphic(iconLight)
      startTime = now
    }

    val elapsedSeconds = (now - startTime) / 1_000_000_000.0

    // Stop the animation after animation duration was reached
    if (elapsedSeconds > animationDuration.toSeconds) {
      labeled.getGraphic.setOpacity(1) // Ensure it ends at full opacity
      labeled.setGraphic(icon)
      tooltip.hide()
      tooltip.setText(tooltipText)
      this.stop()
    } else {
      // if not, oscillate between 0 and 1
      if (direction) {
        alpha = alpha + 0.02
      } else {
        alpha = alpha - 0.02
      }
      if (direction) {
        if (alpha >= 1) {
          direction = false
        }
      } else {
        if (alpha <= 0) {
          direction = true
        }
      }
      labeled.getGraphic.setOpacity(alpha)
    }
  }
}
