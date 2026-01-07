package app.logorrr.docs

import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import javafx.scene.layout.{BorderPane, VBox}
import javafx.scene.paint.Color
import javafx.scene.{Scene, SnapshotParameters}
import javafx.stage.Stage
import net.ladstatt.app.{AppId, AppMeta}
import net.ladstatt.util.io.Fs
import net.ladstatt.util.log.CanLog

import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO

object IconCreatorApp extends Fs with CanLog  {

  def writeIcons(canvases: Seq[(Area, Canvas)], path: Path): Unit = {
    createDirectories(path)
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
    val appMeta = net.ladstatt.app.AppMeta(AppId("IconCreatorApp", "iconcreatorapp", "iconcreator.app"), AppMeta.LogFormat)
    net.ladstatt.app.AppMeta.initApp(appMeta)
    javafx.application.Application.launch(classOf[IconCreatorApp], args*)
  }
}


class IconCreatorApp extends javafx.application.Application {

  val iconSizes: Seq[Int] = Seq(512, 256, 128, 64, 32, 16)
  val icons: Seq[Area] = iconSizes.map(i => Area(i, i, 0, 0))

  def start(stage: Stage): Unit = {
    val bp = new BorderPane()
    val ics =
      for (a@Area(w, h, _, _) <- icons) yield {
        val canvas = new Canvas(w, h)
        val gc2d = canvas.getGraphicsContext2D
        LogorrrIcon.drawIcon(gc2d, w)
        (a, canvas)
      }
    IconCreatorApp.writeIcons(ics, Paths.get("app/src/main/resources/app/logorrr/icon/"))
    IconCreatorApp.writeIcons(ics, Paths.get("dist/dist-linux/flatpak/flatpak-package/src/main/resources/icons/"))
    val box = new VBox(10, ics.map(_._2)*)
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