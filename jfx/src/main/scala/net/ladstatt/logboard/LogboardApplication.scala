package net.ladstatt.logboard

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.stage.Stage


class LogboardApplication {

  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    stage.setTitle("javafx-logboard")
    val lbC = new LogBoardMainBorderPane
    val scene = new Scene(lbC, 1000, 600)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(lbC).foreach(_.setCanvasWidth(t1.intValue))
      }
    })
    stage.setScene(scene)
    stage.show()
  }

}