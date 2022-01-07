package app.logorrr.docs

import app.logorrr.conf.{RecentFileSettings, Settings, SquareImageSettings, StageSettings}
import app.logorrr.util.CanLog
import app.logorrr.views.main.LogoRRRStage
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage

import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO

/**
 * Simple application to take screenshots from a JavaFX app for documentation purposes
 */
object ScreenShotterApp {

  def persistNodeState(node: Node, target: Path): Boolean = {
    Thread.sleep(500)
    val renderedNode = node.snapshot(null, null)
    ImageIO.write(SwingFXUtils.fromFXImage(renderedNode, null), "png", target.toFile)
  }

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[ScreenShotterApp], args: _*)
  }
}

class ScreenShotterApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    for (Area(width, height) <- Area.seq) {
      val settings =
        Settings(StageSettings(0, 0, width, height)
          , SquareImageSettings(7)
          , RecentFileSettings(Seq("logfiles/logic.2.log")))
      val s = LogoRRRStage(stage, settings, getHostServices)
      val bPath = Paths.get(s"docs/releases/${Settings.meta.appVersion}/")
      Files.createDirectories(bPath)
      val f = bPath.resolve(s"${width}x$height.png")
      ScreenShotterApp.persistNodeState(s.stage.getScene.getRoot, f)
      logInfo("created " + f.toAbsolutePath.toString)
      s.show()
    }

  }


}