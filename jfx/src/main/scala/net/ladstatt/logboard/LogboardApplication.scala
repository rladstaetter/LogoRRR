package net.ladstatt.logboard

import javafx.fxml.{FXMLLoader, JavaFXBuilderFactory}
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

/*
object LogboardApplication {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[LogboardApplication], args: _*)
  }
}
*/
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
    val scene = new Scene(parent)
    stage.setScene(scene)
    stage.show()
  }

}