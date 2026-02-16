package app.logorrr.views.util

import javafx.animation.AnimationTimer
import javafx.scene.Node
import javafx.scene.control.{Labeled, Tooltip}

import java.time.Duration

class PulsatingAnimationTimer(labeled: Labeled
                              , icon: Node
                              , iconDark: Node
                              , tooltip: Tooltip
                              , tooltipText: String
                              , animationDuration: Duration) extends AnimationTimer:

  private var startTime: Long = -1
  var alpha: Double = 0.0
  var direction = true

  override def stop(): Unit =
    super.stop()
    labeled.setGraphic(iconDark)
    labeled.setOpacity(1)
    tooltip.hide()

  def handle(now: Long): Unit =
    if startTime < 0 then
      labeled.setGraphic(icon)
      startTime = now

    val elapsedSeconds = (now - startTime) / 1_000_000_000.0

    // Stop the animation after animation duration was reached
    if elapsedSeconds > animationDuration.toSeconds then
      labeled.getGraphic.setOpacity(1) // Ensure it ends at full opacity
      labeled.setGraphic(iconDark)
      tooltip.hide()
      tooltip.setText(tooltipText)
      this.stop()
    else
      // if not, oscillate between 0 and 1
      if direction then
        alpha = alpha + 0.02
      else
        alpha = alpha - 0.02
      if direction then
        if alpha >= 1 then
          direction = false
      else
        if alpha <= 0 then
          direction = true
      labeled.getGraphic.setOpacity(alpha)
