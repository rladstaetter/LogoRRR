package net.ladstatt.logboard

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.stage.Stage

object LogboardApp {

  /** application name */
  val ApplicationName = "javafx-logboard"

  /** initial width of main application scene */
  val InitialSceneWidth = 1000

  /** initial height of main application scene */
  val InitialSceneHeight = 600

  /** width of squares which are painted for each log entry */
  val InitialSquareWidth = 7

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[LogboardApp], args: _*)
  }
}

class LogboardApp extends javafx.application.Application {

  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    stage.setTitle(LogboardApp.ApplicationName)
    val mainBorderPane = new LogBoardMainBorderPane(LogboardApp.InitialSceneWidth, LogboardApp.InitialSquareWidth)
    val scene = new Scene(mainBorderPane, LogboardApp.InitialSceneWidth, LogboardApp.InitialSceneHeight)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(mainBorderPane).foreach(_.setSceneWidth(t1.intValue))
      }
    })
    stage.setScene(scene)
    stage.show()
  }

}