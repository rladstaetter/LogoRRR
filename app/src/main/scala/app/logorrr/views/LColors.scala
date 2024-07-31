package app.logorrr.views

import app.logorrr.util.ColorUtil
import javafx.scene.paint.Color

object LColors {

  val defaultBackgroundColor: Int = ColorUtil.toARGB(Color.WHITE)
  val highlightColor: Color = Color.YELLOW

  val y: Int = ColorUtil.toARGB(highlightColor)
  val yd: Int = ColorUtil.toARGB(highlightColor.darker)
  val yb: Int = ColorUtil.toARGB(highlightColor.brighter)
  val ybb: Int = ColorUtil.toARGB(highlightColor.brighter.brighter)

}
