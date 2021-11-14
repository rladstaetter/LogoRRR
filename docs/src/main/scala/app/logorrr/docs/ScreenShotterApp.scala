package app.logorrr.docs

import app.logorrr.LogoRRRAppBuilder
import app.logorrr.docs.Area.R2560x1600
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage

import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO

object ScreenShotterApp {
  def persistNodeState(node: Node, target: Path) = {
    Thread.sleep(500)
    val renderedNode = node.snapshot(null, null)
    ImageIO.write(SwingFXUtils.fromFXImage(renderedNode, null), "png", target.toFile)
  }

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[ScreenShotterApp], args: _*)
  }
}

class ScreenShotterApp extends javafx.application.Application {

  val iconSizes = Seq(512, 256, 128, 64, 32, 16)

  def start(stage: Stage): Unit = {
    //for (Res(width, height) <- Res.seq) {
    for (Area(width, height) <- Seq[Area](R2560x1600)) {

      val s = LogoRRRAppBuilder.withStage(stage, Seq("logfiles/logic.7.log"), width, height)
      val bPath = Paths.get("docs/releases/21.3.2/")
      Files.createDirectories(bPath)
      val f = bPath.resolve(s"${width}x$height.png")
      ScreenShotterApp.persistNodeState(s.getScene.getRoot, f)
      println("created " + f.toAbsolutePath.toString)

      s.show()
    }

  }


}