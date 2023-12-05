package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.meta.AppMeta
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogoRRRAccelerators
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}


object LogoRRRStage extends CanLog {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

  /** after scene got initialized and scene was set to stage immediately set position of stage */
  val sceneListener: ChangeListener[Scene] = JfxUtils.onNew[Scene](scene => {
    // x, y coordinates of upper left corner from last execution
    val (x, y) = (LogoRRRGlobals.getStageX, LogoRRRGlobals.getStageY)
    scene.getWindow.setX(x)
    scene.getWindow.setY(y)
  })

  def persistSettings(logorrrMain: LogoRRRMain): Unit = {
    // current global state
    // following code can be removed if filters are bound to
    // global mutable Settings
    val settings = LogoRRRGlobals.getSettings

    // to save global filter state
    val activeFilters =
      (for (logFileTab <- logorrrMain.getLogFileTabs) yield {
        logFileTab.pathAsString -> (logFileTab.logFileTabContent.activeFilters, logFileTab.logFileTabContent.getDividerPosition)
      }).toMap

    val updatedSettings =
      for ((p, (fltrs, d)) <- activeFilters) yield {
        p -> settings.logFileSettings(p).copy(filters = fltrs, dividerPosition = d)
      }
    LogoRRRGlobals.persist(settings.copy(logFileSettings = updatedSettings))
  }


  def shutdown(stage: Stage, logorrrMain: LogoRRRMain): Unit = timeR({
    LogoRRRStage.persistSettings(logorrrMain)
    logorrrMain.shutdown()
    LogoRRRGlobals.unbindWindow()
    stage.sceneProperty.removeListener(LogoRRRStage.sceneListener)
    logInfo(s"Stopped ${AppMeta.fullAppNameWithVersion}")
  }, "shutdown")


  def show(stage: Stage, logorrrMain: LogoRRRMain): Unit = {
    stage.show()
    LogoRRRGlobals.getSomeActiveLogFile match {
      case Some(pathAsString) =>
        val tab = logorrrMain.selectLog(pathAsString)
        tab.recalculateChunkListViewAndScrollToActiveElement()
      case None =>
        logorrrMain.selectLastLogFile()
    }
  }

  def init(stage: Stage, logorrrMain: LogoRRRMain) {

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
    stage.setOnCloseRequest((_: WindowEvent) => LogoRRRStage.shutdown(stage, logorrrMain))

    logorrrMain.init()

  }

}


