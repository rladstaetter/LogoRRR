package net.ladstatt.logorrr

import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

abstract class LogSeverity(val name: String, val color: Color)

object LogSeverity {

  case object Info extends LogSeverity("Info", Color.GREEN)

  case object Warning extends LogSeverity("Warning", Color.ORANGE)

  case object Trace extends LogSeverity("Trace", Color.GREY)

  case object Severe extends LogSeverity("Severe", Color.RED)

  case object Other extends LogSeverity("Unclassified", Color.DARKRED)

  val seq: Seq[LogSeverity] = Seq(Info, Trace, Warning, Severe, Other)

  val radii = new CornerRadii(5.0)
  val insets = new Insets(-5.0)

  private def mkBackground(severity: LogSeverity): Background = {
    new Background(new BackgroundFill(severity.color, radii, insets))
  }

  val backgrounds: Map[LogSeverity, Background] = LogSeverity.seq.map {
    ls => ls -> mkBackground(ls)
  }.toMap


}

