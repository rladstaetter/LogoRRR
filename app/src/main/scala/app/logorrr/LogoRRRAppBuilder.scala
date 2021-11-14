package app.logorrr

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}

import java.nio.file.Paths

object LogoRRRAppBuilder {

  def withStage(stage: Stage
                , params: Seq[String]
                , width: Int
                , height: Int): Stage = {
    stage.setTitle(LogoRRRApp.ApplicationName + " " + LogoRRRApp.ApplicationVersion)
    stage.getIcons.add(new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png")))
    val mainBorderPane = new AppMainBorderPane(width, LogoRRRApp.InitialSquareWidth)
    val scene = new Scene(mainBorderPane, width, height)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        println(s"changed: ${t1.intValue}")
        Option(mainBorderPane).foreach(_.setSceneWidth(t1.intValue))
      }
    })
    stage.setScene(scene)


    for (p <- params) {
      mainBorderPane.addLogFile(Paths.get(p).toAbsolutePath)
    }
    mainBorderPane.selectLastLogFile()

    // make sure to cleanup on close
    stage.setOnCloseRequest((_: WindowEvent) => mainBorderPane.shutdown())
    stage
  }
}
