package app.logorrr.docs

import app.logorrr.LogoRRRNative
import app.logorrr.conf._
import app.logorrr.docs.Area._
import app.logorrr.util.CanLog
import app.logorrr.views.main.LogoRRRStage
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage
import pureconfig.ConfigSource

import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

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

  def fromFile(settingsFilePath: Path): Settings = {

    Try(ConfigSource.file(settingsFilePath).loadOrThrow[Settings].filterWithValidPaths()) match {
      case Failure(_) =>
        logWarn(s"Could not load $settingsFilePath, using default settings ...")
        Settings.Default
      case Success(value) =>
        logInfo(s"Loaded settings from $settingsFilePath.")
        value
    }
  }


  def start(stage: Stage): Unit = {
    val s0 = Seq[Area](R1280x800)
    val s1 = Seq[Area](R1440x900)
    val s2 = Seq[Area](R2560x1600)
    val s3 = Seq[Area](R2880x1800)
    for (Area(w, h, width, height) <- s3) {
      val path = Paths.get(s"docs/src/main/resources/screenshotter-$w-$h.conf")
      val settings: Settings =
        fromFile(path)
      val updatedSettings = settings.copy(stageSettings = settings.stageSettings.copy(width = width, height = height))
//       OsxBridge.registerPath(updatedSettings.logFileSettings.values.head.pathAsString)
      LogoRRRGlobals.set(updatedSettings, getHostServices)
      LogoRRRStage(stage).show()
      /*
 val bPath = Paths.get(s"docs/releases/${AppMeta.appVersion}/")
      Fs.createDirectories(bPath)

      val f = bPath.resolve(s"${width}x$height.png")
      Future {
        Thread.sleep(5000)
        JfxUtils.execOnUiThread({
          ScreenShotterApp.persistNodeState(stage.getScene.getRoot, f)
          logInfo(s"created ${f.toAbsolutePath.toString}")
        })
      }

       */
    }

  }


}