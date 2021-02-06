package net.ladstatt.logboard

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

}

abstract class LogSeverity(val name: String, val color: Color)