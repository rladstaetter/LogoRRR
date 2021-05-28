package app.logorrr

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.{Stage, WindowEvent}

import java.nio.file.Paths

object LogoRRRFonts {

  // load font thanks to https://www.jetbrains.com/lp/mono/
  Font.loadFont(getClass.getResource("/app/logorrr/JetBrainsMono-Regular.ttf").toExternalForm, 12)

  def jetBrainsMono(size: Int) =
    s"""|-fx-font-family: 'JetBrains Mono';
        |-fx-font-size: ${size.toString} px;
        |""".stripMargin

}

object LogoRRRApp {

  /** application name */
  val ApplicationName = "LogoRRR"

  /** version which is displayed to user */
  val ApplicationVersion = "21.2.3"

  /** initial width of main application scene */
  val InitialSceneWidth = 1000

  /** initial height of main application scene */
  val InitialSceneHeight = 600

  /** width of squares which are painted for each log entry */
  val InitialSquareWidth = 7

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

}

class LogoRRRApp extends javafx.application.Application {

  import scala.jdk.CollectionConverters._


  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    stage.setTitle(LogoRRRApp.ApplicationName + " " + LogoRRRApp.ApplicationVersion)
    stage.getIcons.add(new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png")))
    val mainBorderPane = new AppMainBorderPane(LogoRRRApp.InitialSceneWidth, LogoRRRApp.InitialSquareWidth)
    val scene = new Scene(mainBorderPane, LogoRRRApp.InitialSceneWidth, LogoRRRApp.InitialSceneHeight)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(mainBorderPane).foreach(_.setSceneWidth(t1.intValue))
      }
    })
    stage.setScene(scene)


    for (p <- getParameters.getRaw.asScala) {
      mainBorderPane.addLogFile(Paths.get(p).toAbsolutePath)
    }
    mainBorderPane.selectLastLogFile()

    // make sure to cleanup on close
    stage.setOnCloseRequest(new EventHandler[WindowEvent] {
      override def handle(event: WindowEvent): Unit = {
        mainBorderPane.shutdown()
      }
    })

    stage.show()

  }

}