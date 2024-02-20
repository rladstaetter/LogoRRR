package app.logorrr.docs

import app.logorrr.conf._
import app.logorrr.docs.Area._
import app.logorrr.io.Fs
import app.logorrr.meta.AppMeta
import app.logorrr.util.CanLog
import app.logorrr.{LogoRRRApp, LogoRRRNative}
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage

import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO

/**
 * Simple application to take screenshots from a JavaFX app for documentation purposes
 */
object ScreenShotterApp {

  def persistNodeState(node: Node, target: Path): Boolean = {
    val renderedNode = node.snapshot(null, null)
    ImageIO.write(SwingFXUtils.fromFXImage(renderedNode, null), "png", target.toFile)
  }

  def main(args: Array[String]): Unit = {
    LogoRRRNative.loadNativeLibraries()
    javafx.application.Application.launch(classOf[ScreenShotterApp], args: _*)
  }
}

class ScreenShotterApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    val s0 = Seq[Area](R1280x800)
    /*
    val s1 = Seq[Area](R1440x900)
    val s2 = Seq[Area](R2560x1600)
    val s3 = Seq[Area](R2880x1800)
    */
    for (Area(w, h, _, _) <- s0) {
      val path = Paths.get(s"docs/src/main/resources/screenshotter-$w-$h.conf")
      val settings: Settings = SettingsIO.fromFile(path)
      val updatedSettings = settings.copy(stageSettings = settings.stageSettings.copy(width = w, height = h + 28))

      LogoRRRApp.start(stage, updatedSettings, getHostServices)

      val bPath = Paths.get(s"docs/releases/${AppMeta.appVersion}/")
      Fs.createDirectories(bPath)

      val f = bPath.resolve(s"${w}x$h.png")
      ScreenShotterApp.persistNodeState(stage.getScene.getRoot, f)
      logInfo(s"created ${f.toAbsolutePath.toString}")

    }

  }


}