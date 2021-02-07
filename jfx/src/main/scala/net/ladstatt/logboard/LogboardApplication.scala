package net.ladstatt.logboard

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.{FXMLLoader, JavaFXBuilderFactory}
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage


class LogboardApplication {


  def mkFxmlLoader(fxmlResource: String): FXMLLoader = {
    val location = getClass.getResource(fxmlResource)
    require(location != null, s"Could not resolve $fxmlResource: Location was null.")
    val fxmlLoader = new FXMLLoader()
    fxmlLoader.setLocation(location)
    fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory())
    fxmlLoader
  }

  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    stage.setTitle("javafx-logboard")
    val fxmlLoader = mkFxmlLoader("/net/ladstatt/logboard/logboard.fxml")
    val parent = fxmlLoader.load[BorderPane]()
    val controller = fxmlLoader.getController[LogboardController]
    val scene = new Scene(parent)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(controller).foreach(_.setCanvasWidth(t1.intValue))
      }
    })
    stage.setScene(scene)
    stage.show()
  }

}