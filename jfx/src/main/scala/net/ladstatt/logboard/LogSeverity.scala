package net.ladstatt.logboard

import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

import java.util
import java.util.Collections

object LogSeverity {

  case object Info extends LogSeverity("Info", Color.GREEN)

  case object Warning extends LogSeverity("Warning", Color.ORANGE)

  case object Trace extends LogSeverity("Trace", Color.GREY)

  case object Severe extends LogSeverity("Severe", Color.RED)

  case object Other extends LogSeverity("Unclassified", Color.BLUE)

  val seq = {
    val l = new util.ArrayList[LogSeverity]()
    Collections.addAll[LogSeverity](l, Info, Warning, Trace, Severe, Other)
    l
  }

  val radii = new CornerRadii(5.0)
  val insets = new Insets(-5.0)

  private def mkBackgrounds(severity: LogSeverity): Background = {
    new Background(new BackgroundFill(severity.color, radii, insets))
  }

  val backgrounds: Map[LogSeverity, Background] =
    Map(LogSeverity.Info -> mkBackgrounds(LogSeverity.Info)
      , LogSeverity.Trace -> mkBackgrounds(LogSeverity.Trace)
      , LogSeverity.Warning -> mkBackgrounds(LogSeverity.Warning)
      , LogSeverity.Severe -> mkBackgrounds(LogSeverity.Severe)
      , LogSeverity.Other -> mkBackgrounds(LogSeverity.Other))


}

abstract class LogSeverity(val name: String, val color: Color)