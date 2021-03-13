package net.ladstatt.logorrr

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.stage.Stage

import java.nio.file.{Files, Paths}

object LogoRRRApp {

  /** application name */
  val ApplicationName = "LogoRRR"

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
    stage.setTitle(LogoRRRApp.ApplicationName)
    val mainBorderPane = new AppMainBorderPane(LogoRRRApp.InitialSceneWidth, LogoRRRApp.InitialSquareWidth)
    val scene = new Scene(mainBorderPane, LogoRRRApp.InitialSceneWidth, LogoRRRApp.InitialSceneHeight)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(mainBorderPane).foreach(_.setSceneWidth(t1.intValue))
      }
    })
    stage.setScene(scene)

    for (p <- getParameters.getRaw.asScala) {
      val path = Paths.get(p).toAbsolutePath
      if (Files.exists(path) && Files.isRegularFile(path)) {
        mainBorderPane.addLogFile(path)
      }
      mainBorderPane.selectLastLogFile()
    }

    stage.show()
  }

}