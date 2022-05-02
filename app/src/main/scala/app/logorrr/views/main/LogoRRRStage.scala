package app.logorrr.views.main

import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.conf.{LogoRRRGlobals, Settings}
import app.logorrr.meta.AppMeta
import app.logorrr.util.JfxUtils
import app.logorrr.views.LogoRRRAccelerators
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}


object LogoRRRStage {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

}

case class LogoRRRStage(stage: Stage
                        , settings: Settings) {

  val logorrrMain = new LogoRRRMain(JfxUtils.closeStage(stage), settings)

  val (width, height) = (LogoRRRGlobals.getStageWidth(), LogoRRRGlobals.getStageHeight())

  val scene = new Scene(logorrrMain, width, height)

  logorrrMain.ambp.sceneWidthProperty.bind(scene.widthProperty())

  /** only initialize accelerators after a scene is defined */
  LogoRRRAccelerators.initAccelerators(scene)
  // bind stage properties (they are initially set and constantly overwritten during execution)
  scene.windowProperty().addListener(MutStageSettings.windowListener)
  stage.sceneProperty().addListener(LogoRRRScene.sceneListener)
  stage.setTitle(AppMeta.fullAppName)
  stage.getIcons.add(LogoRRRStage.icon)
  stage.setScene(scene)

  // make sure to cleanup on close
  stage.setOnCloseRequest((_: WindowEvent) => {
    LogoRRRGlobals.persist()
    logorrrMain.shutdown()
    LogoRRRGlobals.unbindWindow()
    stage.sceneProperty.removeListener(LogoRRRScene.sceneListener)
  })

  def show(): Unit = {
    stage.show()
  }
}
