package app.logorrr.docs

import app.logorrr.conf._
import app.logorrr.conf.mut.MutSettings
import app.logorrr.io.Fs
import app.logorrr.meta.AppMeta
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.DefaultFileIdService
import app.logorrr.services.hostservices.NativeHostServices
import app.logorrr.util.CanLog
import app.logorrr.{LogoRRRApp, LogoRRRNative}
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage

import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO
import scala.util.Try

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

    val args = getParameters.getRaw
    if (args.size() != 1) {
      Console.err.println("Usage: ScreenShotterApp <mode>")
    } else {
      if (Try(args.get(0).toInt).isSuccess) {
        Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
        /*
        val s0 = Seq[Area](R1280x800)
        val s0 = Seq[Area](R1440x900)
        val s0 = Seq[Area](R2560x1600)
        */
        val s0 = Area.seq(args.get(0).toInt)
        for (Area(w, h, _, _) <- Seq(s0)) {
          val path = Paths.get(s"src/main/resources/screenshotter-$w-$h.conf")
          val settings: Settings = SettingsIO.fromFile(path)
          val updatedSettings = settings.copy(stageSettings = settings.stageSettings.copy(width = w, height = h + MutSettings.WindowHeightHack))

          val services = LogoRRRServices(updatedSettings
            , new NativeHostServices(getHostServices)
            , new DefaultFileIdService(() => stage.getScene.getWindow)
            , isUnderTest = false)

          LogoRRRApp.start(stage, services)

          val bPath = Paths.get(s"releases/${AppMeta.appVersion}/")
          Fs.createDirectories(bPath)

          val f = bPath.resolve(s"${w}x$h.png")
          ScreenShotterApp.persistNodeState(stage.getScene.getRoot, f)
          logInfo(s"created ${f.toAbsolutePath.toString}")
        }
      } else {
        Console.err.println(s"Argument has to be a number between 0 and ${Area.seq.length - 1}.")
      }
    }
  }

}