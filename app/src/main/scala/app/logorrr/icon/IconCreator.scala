package app.logorrr.icon

import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.image.WritableImage
import javafx.scene.layout.{BorderPane, VBox}
import javafx.scene.paint.Color
import javafx.scene.{Scene, SnapshotParameters}
import javafx.stage.Stage

import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO

object IconCreatorApp {

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[IconCreatorApp], args: _*)
  }
}

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
    val (x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10) = (xe * 0, xe * 1, xe * 2, xe * 3, xe * 4, xe * 5, xe * 6, xe * 7, xe * 8, xe * 9, xe * 10)
    val (y0, y1, y2, y3, y4, y5, y6, y7, y8, y9, y10) = (ye * 0, ye * 1, ye * 2, ye * 3, ye * 4, ye * 5, ye * 6, ye * 7, ye * 8, ye * 9, ye * 10)

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
    val (x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10) = (xe * 0, xe * 1, xe * 2, xe * 3, xe * 4, xe * 5, xe * 6, xe * 7, xe * 8, xe * 9, xe * 10)
    val (y0, y1, y2, y3, y4, y5, y6, y7, y8, y9, y10) = (ye * 0, ye * 1, ye * 2, ye * 3, ye * 4, ye * 5, ye * 6, ye * 7, ye * 8, ye * 9, ye * 10)

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


class IconCreatorApp extends javafx.application.Application {

  val iconSizes = Seq(512, 256, 128, 64, 32, 16)

  def start(stage: Stage): Unit = {
    val bp = new BorderPane()
    val icons =
      for (size <- iconSizes) yield {
        val canvas = new Canvas(size, size)
        val gc2d = canvas.getGraphicsContext2D
        LogorrrIcon.drawIcon(gc2d, size)
        (size, canvas)
      }
    writeIcons(icons, Paths.get("app/src/main/resources/app/logorrr/icon/"))
    val box = new VBox(10, icons.map(_._2): _*)
    box.setAlignment(Pos.CENTER)
    bp.setCenter(box)
    val scene = new Scene(bp, iconSizes.max + iconSizes.max / 10, iconSizes.sum + iconSizes.sum / 10)
    stage.setScene(scene)
    stage.show()
  }

  def writeIcons(canvases: Seq[(Int, Canvas)], path: Path): Unit = {
    Files.createDirectories(path)
    for ((s, c) <- canvases) {
      val file = path.resolve(s"logorrr-icon-${s}.png")
      writeIcon(c, file)
    }
  }


  def writeIcon(c: Canvas, target: Path): Unit = {
    val writableImage = new WritableImage(c.getWidth.toInt, c.getHeight.toInt)
    val spp = new SnapshotParameters()
    spp.setFill(Color.TRANSPARENT)
    c.snapshot(spp, writableImage)
    val renderedImage = SwingFXUtils.fromFXImage(writableImage, null)
    ImageIO.write(renderedImage, "png", target.toFile)
  }


  def drawRoundedRectangleWithShadow(gc2d: GraphicsContext
                                     , color: Color
                                     , x: Int
                                     , y: Int
                                     , size: Int): Unit = {
    gc2d.setFill(color.darker)
    gc2d.fillRoundRect(x, y, size, size, size.toDouble / 10, size.toDouble / 10)
    gc2d.setFill(color)
    gc2d.fillRoundRect(x, y, size - 1, size - 1, size.toDouble / 10, size.toDouble / 10)
  }

  private def fillRect(gc2d: GraphicsContext, x: Int, y: Int, w: Int, h: Int, fill: Color) = {
    gc2d.beginPath()
    gc2d.setFill(fill)
    gc2d.fillRect(x, y, w, h)
    gc2d.closePath()
  }
}