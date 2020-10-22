package net.ladstatt.logboard

import java.nio.file.{Files, Paths}

import javafx.application.{Application, Platform}
import javafx.fxml.{FXMLLoader, JavaFXBuilderFactory}
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.jdk.CollectionConverters.CollectionHasAsScala

class LogboardApp  {

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
  def start(stage: Stage, parameters: Application.Parameters): Unit = {
    stage.setTitle("LogboardController")
    val params = parameters.getRaw.asScala
    params.headOption match {
      case Some(logFile) =>
        val fxmlLoader = mkFxmlLoader("/net/ladstatt/logboard/logboard.fxml")
        val parent = fxmlLoader.load[BorderPane]()
        val controller = fxmlLoader.getController[LogboardController]
        controller.setLogEntries(Files.readAllLines(Paths.get(logFile)).toVector)
        val scene = new Scene(parent)

        stage.setScene(scene)
        stage.show()
      case None =>
        System.err.println("Usage: LogboardController <logfile>")
        Platform.exit()
    }

  }

}