package app.logorrr.clv.color

import javafx.scene.paint.Color

object LColors:

  val defaultBackgroundColor: Int = ColorUtil.toARGB(Color.WHITE)
  private val yellow: Color = Color.YELLOW

  val y: Int = ColorUtil.toARGB(yellow)
  val yd: Int = ColorUtil.toARGB(yellow.darker)
  val yb: Int = ColorUtil.toARGB(yellow.brighter)
  val ybb: Int = ColorUtil.toARGB(yellow.brighter.brighter)

  val cyan = ColorUtil.toARGB(Color.CYAN)
  val turquoise = ColorUtil.toARGB(Color.TURQUOISE)
  val indigo = ColorUtil.toARGB(Color.INDIGO)
  
  val color0 = BlockColor(y, yb, yb, yd, yd)
  val color1 = BlockColor(turquoise, ybb, ybb, yb, y)
  val color2 = BlockColor(indigo, ybb, ybb, y, y)
/*
val color0 = BlockColor(y, yb, yb, yd, yd)
val color1 = BlockColor(yb, ybb, ybb, yb, y)
val color2 = BlockColor(yb, ybb, ybb, y, y)
*/
