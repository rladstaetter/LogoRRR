package net.ladstatt.logboard

import javafx.scene.paint.Color

object LogSeverity {

  case object Info extends LogSeverity(Color.GREEN)

  case object Warning extends LogSeverity(Color.YELLOW)

  case object Trace extends LogSeverity(Color.GREY)

  case object Severe extends LogSeverity(Color.RED)

  case object Other extends LogSeverity(Color.BLUE)

  val seq = Seq[LogSeverity](Info, Warning, Trace, Severe, Other)
}

abstract class LogSeverity(val color: Color)