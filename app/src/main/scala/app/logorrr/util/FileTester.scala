package app.logorrr.util

import app.logorrr.conf.Settings
import app.logorrr.conf.Settings.{Default, renderOptions}
import app.logorrr.io.Fs
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea}
import javafx.scene.layout.BorderPane
import javafx.stage.{DirectoryChooser, Stage}
import pureconfig.ConfigWriter
import pureconfig.generic.auto._

import java.io.File
import scala.util.{Failure, Success, Try}

/**
 * for debugging purposes
 */
object FileTester {

  def withStage(stage: Stage
                , params: Seq[String]
                , settings: Settings): Stage = {
    val mainBorderPane = new BorderPane()
    val bb = new Button("mumu")
    val b = new Button("write")
    val t = new TextArea()
    bb.setOnAction(e => {
      t.appendText("SYSTEM start ---")
      t.appendText(new String(LogUtil.outBackingStream.toByteArray, StringUtil.utf8))
      t.appendText("SYSTEM end ---")

      t.appendText("ERROR start ---")
      t.appendText(new String(LogUtil.errBackingStream.toByteArray, StringUtil.utf8))
      t.appendText("ERROR end ---")
    })
    b.setOnAction(e => {
      val dc = new DirectoryChooser()
      val directory: File = dc.showDialog(null)
      val filePath = directory.toPath.resolve("test.txt")
      Try {
        Fs.write(filePath, ConfigWriter[Settings].to(Default).render(renderOptions))
      } match {
        case Failure(exception) =>
          t.appendText(exception.getMessage)
        case Success(value) =>
          t.appendText(s"Success: ${filePath.toAbsolutePath}\n")
      }
    })
    mainBorderPane.setBottom(t)
    mainBorderPane.setLeft(bb)
    mainBorderPane.setRight(b)
    val scene = new Scene(mainBorderPane, settings.stageSettings.width, settings.stageSettings.height)
    stage.setScene(scene)
    stage
  }
}
