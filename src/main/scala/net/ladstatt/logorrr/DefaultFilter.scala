package net.ladstatt.logorrr

import javafx.scene.paint.Color

object DefaultFilter {

  val trace = CaseInsensitiveFilter("Trace", Color.GREY)
  val info = CaseInsensitiveFilter("Info", Color.GREEN)
  val warning = CaseInsensitiveFilter("Warning", Color.ORANGE)
  val severe = CaseInsensitiveFilter("Severe", Color.RED)

  val seq: Seq[Filter] = Seq(trace, info, warning, severe)

}
