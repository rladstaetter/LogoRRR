package app.logorrr.docs

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

/**
 * Encodes logic to paint our LogoRRR logo.
 */
object LogorrrIcon {

  def drawRoundedRectangleWithShadow(gc2d: GraphicsContext)
                                    (color: Color
                                     , x: Double
                                     , y: Double
                                     , width: Double
                                     , height: Double
                                     , round: Double): Unit = {
    gc2d.setFill(color.darker)
    gc2d.fillRoundRect(x, y, width, height, round, round)
    gc2d.setFill(color)
    gc2d.fillRoundRect(x, y, width - 1, height - 1, round, round)
  }

  def drawIcon(graphicsContext: GraphicsContext, size: Int): Unit = {
    val rws = LogorrrIcon.drawRoundedRectangleWithShadow(graphicsContext) _

    val s = size.toDouble
    val factor = 10
    val round = s / factor
    val xe = s / factor
    val ye = s / factor
    val (x0,  _, x2,  _, _, _, _, x7, x8,  _, x10) = (xe * 0, xe * 1, xe * 2, xe * 3, xe * 4, xe * 5, xe * 6, xe * 7, xe * 8, xe * 9, xe * 10)
    val (y0, y1,  _, y3, _, _, _,  _, y8, y9,   _) = (ye * 0, ye * 1, ye * 2, ye * 3, ye * 4, ye * 5, ye * 6, ye * 7, ye * 8, ye * 9, ye * 10)

    //rws(Color.WHITE, x0, y0, s, s, round)
    //drawRaster(Color.GREENYELLOW, x0, y0, s, s, factor)
    val c = Color.BEIGE
    rws(c.darker(), x2 + xe / 2, y3 + ye / 2, 5 * xe, 4 * ye, round)
    rws(c.darker().darker(), x0, y0, 2 * xe, y9, round)
    rws(c.darker().darker().darker(), x0, y8, x10, 2 * ye, round)
    // rws(Color.RED, x2, y0, 2*xe, y6, factor)
    rws(c.darker().darker().darker().darker(), x8, y1, 2 * xe, y9, round)
    rws(c.darker().darker().darker().darker().darker(), x2 + xe / 2, y1, x7 + xe / 2, 2 * ye, round)
  }

  def drawIcon2(graphicsContext: GraphicsContext, size: Int): Unit = {
    val rws = LogorrrIcon.drawRoundedRectangleWithShadow(graphicsContext) _

    val s = size.toDouble
    val factor = 10
    val round = s / factor
    val xe = s / factor
    val ye = s / factor
    val (x0, _, x2,  _, _, _, _, x7, x8,  _, x10) = (xe * 0, xe * 1, xe * 2, xe * 3, xe * 4, xe * 5, xe * 6, xe * 7, xe * 8, xe * 9, xe * 10)
    val (y0, y1, _, y3, _, _, _,  _, y8, y9,   _) = (ye * 0, ye * 1, ye * 2, ye * 3, ye * 4, ye * 5, ye * 6, ye * 7, ye * 8, ye * 9, ye * 10)

    //rws(Color.WHITE, x0, y0, s, s, round)
    //drawRaster(Color.GREENYELLOW, x0, y0, s, s, factor)
    val c = Color.BLACK
    rws(c, x2 + xe / 2, y3 + ye / 2, 5 * xe, 4 * ye, round)
    rws(c, x0, y0, 2 * xe, y9, round)
    rws(c, x0, y8, x10, 2 * ye, round)
    // rws(Color.RED, x2, y0, 2*xe, y6, factor)
    rws(c, x8, y1, 2 * xe, y9, round)
    rws(c, x2 + xe / 2, y1, x7 + xe / 2, 2 * ye, round)
  }

}
