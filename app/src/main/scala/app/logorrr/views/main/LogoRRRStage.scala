package app.logorrr.views.main

import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.conf.{LogoRRRGlobals, RecentFileSettings, Settings, StageSettings}
import app.logorrr.meta.AppMeta
import app.logorrr.util.JfxUtils
import javafx.application.HostServices
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}


object LogoRRRStage {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

  def apply(stage: Stage
            , settings: Settings
            , hs: HostServices): LogoRRRStage = {
    new LogoRRRStage(stage, settings.stageSettings, settings.recentFileSettings, hs)
  }

}

class LogoRRRStage(val stage: Stage
                   , initialStageSettings: StageSettings
                   , recentFileSettings: RecentFileSettings
                   , hs: HostServices) {

  val logorrrMain = new LogoRRRMain(hs, JfxUtils.closeStage(stage), initialStageSettings, recentFileSettings)
  val scene = new Scene(logorrrMain, initialStageSettings.width, initialStageSettings.height)
  logorrrMain.ambp.sceneWidthProperty.bind(scene.widthProperty())

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
    logorrrMain.shutdown()
    MutStageSettings.unbind()
    stage.sceneProperty.removeListener(sceneListener)
  })

  def show(): Unit = {
    stage.show()
  }
}
