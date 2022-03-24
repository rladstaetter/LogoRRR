package app.logorrr.views.main

import app.logorrr.conf.mut.{MutSettings, MutStageSettings}
import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.meta.AppMeta
import app.logorrr.util.JfxUtils
import javafx.application.HostServices
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}

object LogoRRRGlobals {

  val settings = new MutSettings

}

object LogoRRRStage {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

  def apply(stage: Stage
            , settings: Settings
            , hs: HostServices): LogoRRRStage = {
    LogoRRRGlobals.settings.setRecentFileSettings(settings.recentFileSettings)
    LogoRRRGlobals.settings.setSquareImageSettings(settings.squareImageSettings)
    LogoRRRGlobals.settings.setStageSettings(settings.stageSettings)
    val ls = new LogoRRRStage(stage, settings.stageSettings, settings, hs)
    ls
  }

}

class LogoRRRStage(val stage: Stage
                   , initialStageSettings: StageSettings
                   , settings: Settings
                   , hs: HostServices) {

  val mainPane = new LogoRRRMain(hs, JfxUtils.closeStage(stage), initialStageSettings, settings.squareImageSettings, settings.recentFileSettings)
  val scene = LogoRRRScene(mainPane, initialStageSettings)

  LogoRRRGlobals.settings.squareImageSettings.widthProperty.bind(mainPane.ambp.squareWidthProperty)


  /** after scene got initialized / scene was set to stage immediately set position of stage */
  val sceneListener = LogoRRRScene.mkSceneListener(initialStageSettings.x, initialStageSettings.y)()

  // bind stage properties (they are initially set and constantly overwritten during execution)
  scene.windowProperty().addListener(MutStageSettings.windowListener)
  stage.sceneProperty().addListener(sceneListener)
  stage.setTitle(AppMeta.fullAppName)
  stage.getIcons.add(LogoRRRStage.icon)
  stage.setScene(scene)

  // make sure to cleanup on close
  stage.setOnCloseRequest((_: WindowEvent) => {
    mainPane.shutdown()
    MutStageSettings.unbind()
    stage.sceneProperty.removeListener(sceneListener)
  })

  def show(): Unit = {
    stage.show()
  }
}
