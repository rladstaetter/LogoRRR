package app.logorrr.docs

import app.logorrr.conf._
import app.logorrr.meta.AppMeta
import app.logorrr.model.LogFileSettings
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.main.LogoRRRStage
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage

import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Simple application to take screenshots from a JavaFX app for documentation purposes
 */
object ScreenShotterApp {

  def persistNodeState(node: Node, target: Path): Boolean = {
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
      val path = Paths.get("logfiles/logic.2.log").toAbsolutePath
      val settings =
        Settings(StageSettings(0, 0, width, height)
          , Map(path.toAbsolutePath.toString -> LogFileSettings(path))
          , Option(path.toAbsolutePath.toString))
      LogoRRRGlobals.set(settings, getHostServices)
      val bPath = Paths.get(s"docs/releases/${AppMeta.appVersion}/")
      Files.createDirectories(bPath)
      val f = bPath.resolve(s"${width}x$height.png")
       LogoRRRStage(stage).show()
      Future {
        Thread.sleep(5000)
        JfxUtils.execOnUiThread({
          ScreenShotterApp.persistNodeState(stage.getScene.getRoot, f)
          logInfo("created " + f.toAbsolutePath.toString)
        })
      }
    }

  }


}