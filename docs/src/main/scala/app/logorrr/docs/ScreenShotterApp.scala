package app.logorrr.docs

import app.logorrr.LogoRRRApp
import app.logorrr.conf.*
import app.logorrr.io.SettingsFileIO
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.DefaultFileIdService
import app.logorrr.services.hostservices.NativeHostServices
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Stage
import net.ladstatt.util.io.TinyIo
import net.ladstatt.util.log.TinyLog

import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO
import scala.util.Try

/**
 * Simple application to take screenshots from a JavaFX app for documentation purposes
 */
object ScreenShotterApp:

  def persistNodeState(node: Node, target: Path): Boolean =
    val renderedNode = node.snapshot(null, null)
    ImageIO.write(SwingFXUtils.fromFXImage(renderedNode, null), "png", target.toFile)

  def main(args: Array[String]): Unit =
    TinyLog.init(Paths.get("target/simplelog.log"))
    javafx.application.Application.launch(classOf[ScreenShotterApp], args *)


class ScreenShotterApp extends javafx.application.Application with TinyIo with TinyLog:

  def start(stage: Stage): Unit =
    val args = getParameters.getRaw
    if args.size() != 1 then
      Console.err.println("Usage: ScreenShotterApp <mode>")
    else if Try(args.get(0).toInt).isSuccess then
      val s0 = Area.seq(args.get(0).toInt)
      for (Area(w, h, _, _) <- Seq(s0)) do
        val path = Paths.get(s"docs/src/main/resources/screenshotter-$w-$h.json")
        val settings: Settings = SettingsFileIO.fromFile(path)
        val services = LogoRRRServices(settings
          , new NativeHostServices(getHostServices)
          , new DefaultFileIdService(() => stage.getScene.getWindow)
          , isUnderTest = false)

        LogoRRRApp.start(stage, services)

        val bPath = Paths.get(s"releases/${AppInfo.appVersion}/")
        createDirectories(bPath)
        val f = bPath.resolve(s"${w}x$h.png")
        ScreenShotterApp.persistNodeState(stage.getScene.getRoot, f)
        logInfo(s"created ${f.toAbsolutePath.toString}")
    else
      Console.err.println(s"Argument has to be a number between 0 and ${Area.seq.length - 1}.")


