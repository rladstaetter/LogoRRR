package app.logorrr.clv.color

import javafx.scene.paint.Color

object LColors {

  val defaultBackgroundColor: Int = ColorUtil.toARGB(Color.WHITE)
  private val yellow: Color = Color.YELLOW

  val y: Int = ColorUtil.toARGB(yellow)
  val yd: Int = ColorUtil.toARGB(yellow.darker)
  val yb: Int = ColorUtil.toARGB(yellow.brighter)
  val ybb: Int = ColorUtil.toARGB(yellow.brighter.brighter)

  val color0 = BlockColor(LColors.y, LColors.yb, LColors.yb, LColors.yd, LColors.yd)
  val color1 = BlockColor(LColors.yb, LColors.ybb, LColors.ybb, LColors.yb, LColors.y)
  val color2 = BlockColor(LColors.yb, LColors.ybb, LColors.ybb, LColors.y, LColors.y)

}
