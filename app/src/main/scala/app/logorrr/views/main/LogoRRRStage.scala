package app.logorrr.views.main

import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.conf.mut.{MutSettings, MutStageSettings}
import app.logorrr.meta.AppMeta
import app.logorrr.util.JfxUtils
import javafx.application.HostServices
import javafx.scene.image.Image
import javafx.stage.{Stage, Window, WindowEvent}

object LG {

  val settings = new MutSettings

}

object LogoRRRStage {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

  def apply(stage: Stage
            , settings: Settings
            , hs: HostServices): LogoRRRStage = {
    LG.settings.setRecentFileSettings(settings.recentFileSettings)
    LG.settings.setSquareImageSettings(settings.squareImageSettings)
    LG.settings.setStageSettings(settings.stageSettings)
    val ls = new LogoRRRStage(stage, settings.stageSettings, settings, hs)
    ls
  }

}

class LogoRRRStage(val stage: Stage
                   , initialStageSettings: StageSettings
                   , settings: Settings
                   , hs: HostServices) {

  val mainPane = new LogoRRRMain(hs, JfxUtils.closeStage(stage), settings)
  val scene = LogoRRRScene(mainPane, initialStageSettings)

  /** after scene got initialized / scene was set to stage immediately set position of stage */
  val sceneListener = LogoRRRScene.mkSceneListener(initialStageSettings.x, initialStageSettings.y)()

  val abbWidthListener = JfxUtils.onNew[Number](width => mainPane.setSceneWidth(width.intValue))

  def show(): Unit = {
    stage.sceneProperty().addListener(sceneListener)
    scene.widthProperty().addListener(abbWidthListener)
    stage.setTitle(AppMeta.fullAppName)
    stage.getIcons.add(LogoRRRStage.icon)
    stage.setScene(scene)

    // make sure to cleanup on close
    stage.setOnCloseRequest((_: WindowEvent) => {
      mainPane.shutdown()
      MutStageSettings.unbind()
      scene.widthProperty().removeListener(abbWidthListener)
      stage.sceneProperty.removeListener(sceneListener)
    })
    stage.show()
  }
}
