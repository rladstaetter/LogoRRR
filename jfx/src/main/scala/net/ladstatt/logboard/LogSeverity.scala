package net.ladstatt.logboard

import javafx.scene.paint.Color

object LogSeverity {

  case object Info extends LogSeverity("Info", Color.GREEN)

  case object Warning extends LogSeverity("Warning", Color.YELLOW)

  case object Trace extends LogSeverity("Trace", Color.GREY)

  case object Severe extends LogSeverity("Severe", Color.RED)

  case object Other extends LogSeverity("Unclassified", Color.BLUE)

  val seq = Seq[LogSeverity](Info, Warning, Trace, Severe, Other)
}

abstract class LogSeverity(val name: String, val color: Color)