package net.ladstatt.logboard

import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

import java.util

abstract class LogSeverity(val name: String, val color: Color)

object LogSeverity {

  case object Info extends LogSeverity("Info", Color.GREEN)

  case object Warning extends LogSeverity("Warning", Color.ORANGE)

  case object Trace extends LogSeverity("Trace", Color.GREY)

  case object Severe extends LogSeverity("Severe", Color.RED)

  case object Other extends LogSeverity("Unclassified", Color.BLUE)

  val seq: Seq[LogSeverity] = Seq(Info, Warning, Trace, Severe, Other)
  /*
  val seq: util.ArrayList[LogSeverity] = {
    val l = new util.ArrayList[LogSeverity]()
    Collections.addAll[LogSeverity](l, Info, Warning, Trace, Severe, Other)
    l
  }*/

  val radii = new CornerRadii(5.0)
  val insets = new Insets(-5.0)

  private def mkBackground(severity: LogSeverity): Background = {
    new Background(new BackgroundFill(severity.color, radii, insets))
  }

  val backgrounds: util.HashMap[LogSeverity, Background] = {
    val backgrounds = new util.HashMap[LogSeverity, Background]()
    backgrounds.put(LogSeverity.Info, mkBackground(LogSeverity.Info))
    backgrounds.put(LogSeverity.Trace, mkBackground(LogSeverity.Trace))
    backgrounds.put(LogSeverity.Warning, mkBackground(LogSeverity.Warning))
    backgrounds.put(LogSeverity.Severe, mkBackground(LogSeverity.Severe))
    backgrounds.put(LogSeverity.Other, mkBackground(LogSeverity.Other))
    backgrounds
  }


}

