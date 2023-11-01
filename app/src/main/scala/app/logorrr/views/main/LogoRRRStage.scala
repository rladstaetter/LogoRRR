package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.meta.AppMeta
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogoRRRAccelerators
import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}


object LogoRRRStage {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))
  /** after scene got initialized and scene was set to stage immediately set position of stage */
  val sceneListener: ChangeListener[Scene] = JfxUtils.onNew[Scene](scene => {
    // x, y coordinates of upper left corner from last execution
    val (x, y) = (LogoRRRGlobals.getStageX, LogoRRRGlobals.getStageY)
    scene.getWindow.setX(x)
    scene.getWindow.setY(y)
  })

}

case class LogoRRRStage(stage: Stage) extends CanLog {

  private val logorrrMain = new LogoRRRMain(JfxUtils.closeStage(stage))

  val (width, height) = (LogoRRRGlobals.getStageWidth, LogoRRRGlobals.getStageHeight)

  val scene = new Scene(logorrrMain, width, height)

  /** only initialize accelerators after a scene is defined */
  LogoRRRAccelerators.initAccelerators(scene)
  // bind stage properties (they are initially set and constantly overwritten during execution)
  scene.windowProperty().addListener(MutStageSettings.windowListener)
  stage.sceneProperty().addListener(LogoRRRStage.sceneListener)
  stage.setTitle(AppMeta.fullAppName)
  stage.getIcons.add(LogoRRRStage.icon)
  stage.setScene(scene)
  private val stylesheet: String = new PrimerLight().getUserAgentStylesheet
  // Application.setUserAgentStylesheet(stylesheet)
  stage.setOnCloseRequest((_: WindowEvent) => shutdown())

  private def shutdown(): Unit = timeR({
    persistSettings()

    logorrrMain.shutdown()
    LogoRRRGlobals.unbindWindow()
    stage.sceneProperty.removeListener(LogoRRRStage.sceneListener)
  }

    , s"Stopped ${AppMeta.fullAppNameWithVersion}"
  )

  private def persistSettings(): Unit = {
    // current global state
    // follwoing code can be removed if filters are bound to
    // global mutable Settings
    val settings = LogoRRRGlobals.getSettings

    // to save global filter state
    val activeFilters =
      (for (logFileTab <- logorrrMain.getLogFileTabs) yield {
        logFileTab.pathAsString -> logFileTab.activeFilters
      }).toMap

    val updatedSettings =
      for ((p, fltrs) <- activeFilters) yield {
        p -> settings.logFileSettings(p).copy(filters = fltrs)
      }
    LogoRRRGlobals.persist(settings.copy(logFileSettings = updatedSettings))
  }

  def show(): Unit = {
    stage.show()
  }
}
