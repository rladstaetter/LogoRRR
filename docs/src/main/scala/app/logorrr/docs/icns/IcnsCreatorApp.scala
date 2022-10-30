package app.logorrr.docs.icns

import app.logorrr.build.Commander
import app.logorrr.docs.{Area, IconCreatorApp, LogorrrIcon}
import app.logorrr.io.Fs
import javafx.scene.canvas.Canvas
import javafx.stage.Stage

import java.nio.file.{Files, Path, Paths}

object IcnsCreatorApp {

  val iconUtil = Paths.get("/usr/bin/iconutil")

  /**
   * @return true if all helper programs exist
   */
  def checkPrerequisites(): Boolean = {
    val existsIconutil = Files.exists(iconUtil)
    if (!existsIconutil) {
      System.err.println(s"Could not find ${iconUtil.toAbsolutePath}.")
    }
    existsIconutil
  }

  def main(args: Array[String]): Unit = {
    if (checkPrerequisites()) {
      javafx.application.Application.launch(classOf[IcnsCreatorApp], args: _*)
    } else {
      System.err.println("Could not start IconCreatorApp since prerequisites failed.")
    }
  }
}


/**
 * Creates a icns file which is suitable for the app store
 *
 * We need JavaFX functionality to create our icons, this is why we need
 * to invoke a JavaFX environment.
 *
 * see https://stackoverflow.com/questions/12306223/how-to-manually-create-icns-files-using-iconutil on how to generate icns files
 *
 */
class IcnsCreatorApp extends javafx.application.Application {

  def perform(): Unit = {
    // generate sudirectory with images
    val name = "logorrr-iconset.iconset"
    val iconSetDir = Paths.get(s"dist/dist-osx/installer-osx/src/main/resources/icon/$name")
    // create images
    generateIcons(iconSetDir)
    // call iconutil

    val cmds = Seq(IcnsCreatorApp.iconUtil.toAbsolutePath.toString
      , "-c"
      , "icns"
      , iconSetDir.toAbsolutePath.toString)
    Commander.execCmd(Paths.get("."), cmds)
    // remove temporary directory
  }

  def generateIcons(targetPath: Path): Unit = {
    Fs.createDirectories(targetPath)
    for (IconDef(Area(w, h), name) <- IconDef.seq) {
      val canvas = new Canvas(w, h)
      val gc2d = canvas.getGraphicsContext2D
      LogorrrIcon.drawIcon(gc2d, w)
      IconCreatorApp.writeIcon(canvas, targetPath.resolve(name))
    }
  }

  override def start(primaryStage: Stage): Unit = {
    perform()
    System.exit(0)
  }
}
