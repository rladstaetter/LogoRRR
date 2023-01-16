package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.meta.AppMeta
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogoRRRAccelerators
import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}


object LogoRRRStage {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

}

case class LogoRRRStage(stage: Stage) extends CanLog {

  val logorrrMain = new LogoRRRMain(JfxUtils.closeStage(stage))

  val (width, height) = (LogoRRRGlobals.getStageWidth(), LogoRRRGlobals.getStageHeight())

  val scene = new Scene(logorrrMain, width, height)

  /** only initialize accelerators after a scene is defined */
  LogoRRRAccelerators.initAccelerators(scene)
  // bind stage properties (they are initially set and constantly overwritten during execution)
  scene.windowProperty().addListener(MutStageSettings.windowListener)
  stage.sceneProperty().addListener(LogoRRRScene.sceneListener)
  stage.setTitle(AppMeta.fullAppName)
  stage.getIcons.add(LogoRRRStage.icon)
  stage.setScene(scene)
  Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet)
  stage.setOnCloseRequest((_: WindowEvent) => closeApp())

  private def closeApp(): Unit = {
    LogoRRRGlobals.persist()
    logorrrMain.shutdown()
    LogoRRRGlobals.unbindWindow()
    stage.sceneProperty.removeListener(LogoRRRScene.sceneListener)
    logInfo(s"Stopped " + AppMeta.fullAppNameWithVersion)
  }

  def show(): Unit = {
    stage.show()
  }
}
