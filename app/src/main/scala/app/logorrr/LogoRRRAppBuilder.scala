package app.logorrr

import app.logorrr.conf.Settings
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.stage.{Stage, WindowEvent}

import java.nio.file.Paths

object LogoRRRAppBuilder {

  def withStage(stage: Stage
                , params: Seq[String]
                , settings: Settings): Stage = {
    stage.setTitle(Settings.meta.appName + " " + Settings.meta.appVersion)
    stage.getIcons.add(Settings.icon)
    val mainBorderPane = new AppMainBorderPane(settings.stageSettings.width, settings.squareImageSettings.width)
    val scene = new Scene(mainBorderPane, settings.stageSettings.width, settings.stageSettings.height)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
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
