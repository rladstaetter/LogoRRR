package net.ladstatt.logorrr

import javafx.scene.paint.Color

object DefaultFilter {

  val finest = ExactMatchFilter("FINEST", Color.GREY)
  val info = ExactMatchFilter("INFO", Color.GREEN)
  val warning = ExactMatchFilter("WARNING", Color.ORANGE)
  val severe = ExactMatchFilter("SEVERE", Color.RED)

  val seq: Seq[Filter] = Seq(finest, info, warning, severe)

}
