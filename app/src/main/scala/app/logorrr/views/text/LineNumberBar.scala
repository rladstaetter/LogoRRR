package app.logorrr.views.text

import app.logorrr.model.LogEntry
import javafx.scene.layout.HBox
import javafx.scene.shape.Rectangle

import java.time.Instant
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

object LineNumberBar {
  val maxDuration: FiniteDuration = 1200 millis

}

case class LineNumberBar(e: LogEntry
                         , instant: Instant
                         , timings: Map[Int, Instant]
                        ) extends HBox {


  val duration: Long =
    timings.get(e.lineNumber + 1) match {
      case Some(nextTi) =>
        nextTi.toEpochMilli - instant.toEpochMilli
      case None =>
        0L
    }

  if (duration > LineNumberBar.maxDuration.toMillis) {
    val left = new Rectangle(LineNumberBar.maxDuration.toMillis, 10)
    left.setFill(LogTextView.timeBarColor)
    val right = new Rectangle(2, 10)
    right.setFill(LogTextView.timeBarOverflowColor)
    getChildren.addAll(left, right)
  } else {
    val left = new Rectangle(duration.toDouble, 10)
    left.setFill(LogTextView.timeBarColor)
    getChildren.addAll(left)
  }
}
