package app.logorrr.docs

import app.logorrr.io.Fs
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import javafx.scene.layout.{BorderPane, VBox}
import javafx.scene.paint.Color
import javafx.scene.{Scene, SnapshotParameters}
import javafx.stage.Stage

import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO

object IconCreatorApp {

  def writeIcons(canvases: Seq[(Area, Canvas)], path: Path): Unit = {
    Fs.createDirectories(path)
    for ((Area(s, _, _, _), c) <- canvases) {
      val file = path.resolve(s"logorrr-icon-$s.png")
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
    Console.println(s"Wrote '${target.toAbsolutePath.toString}'.")
  }

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[IconCreatorApp], args: _*)
  }
}


class IconCreatorApp extends javafx.application.Application {

  val iconSizes: Seq[Int] = Seq(720)
  val icons: Seq[Area] = iconSizes.map(i => Area(i, i,0,0))

  def start(stage: Stage): Unit = {
    val bp = new BorderPane()
    val ics =
      for (a@Area(w, h,_,_) <- icons) yield {
        val canvas = new Canvas(w, 1080)
        val gc2d = canvas.getGraphicsContext2D
        LogorrrIcon.drawIcon(gc2d, w)
        (a, canvas)
      }
    IconCreatorApp.writeIcons(ics, Paths.get("app/src/main/resources/app/logorrr/icon/"))
    val box = new VBox(10, ics.map(_._2): _*)
    box.setAlignment(Pos.CENTER)
    bp.setCenter(box)
    val scene = new Scene(bp, iconSizes.max + iconSizes.max / 10, iconSizes.sum + iconSizes.sum / 10)
    stage.setScene(scene)
    stage.show()
  }

  /*
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
    */
}