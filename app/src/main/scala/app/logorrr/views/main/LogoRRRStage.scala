package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.io.FileId
import app.logorrr.meta.AppInfo
import app.logorrr.util.JfxUtils
import app.logorrr.views.{SearchTerm, LogoRRRAccelerators}
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}
import net.ladstatt.util.log.CanLog


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
    val activeFilters: Map[FileId, (Seq[SearchTerm], Double)] =
      (for (logFileTab <- logorrrMain.getLogFileTabs) yield {
        logFileTab.fileId -> (logFileTab.logFileTabContent.activeFilters, logFileTab.logFileTabContent.getDividerPosition)
      }).toMap

    val updatedSettings =
      for ((p, (fltrs, d)) <- activeFilters) yield {
        p.absolutePathAsString -> settings.fileSettings(p.absolutePathAsString).copy(searchTerms = fltrs, dividerPosition = d)
      }
    LogoRRRGlobals.persist(settings.copy(fileSettings = updatedSettings))
  }


  def shutdown(stage: Stage, logorrrMain: LogoRRRMain): Unit = timeR({
    LogoRRRStage.persistSettings(logorrrMain)
    logorrrMain.shutdown()
    LogoRRRGlobals.unbindWindow()
    stage.sceneProperty.removeListener(LogoRRRStage.sceneListener)
    logInfo(s"Stopped ${AppInfo.fullAppNameWithVersion}")
  }, "shutdown")


  def selectActiveLogFile(logorrrMain: LogoRRRMain): Unit = {
    LogoRRRGlobals.getSomeActiveLogFile match {
      case Some(fileId) =>
        if (logorrrMain.contains(fileId)) {
          val tab = logorrrMain.selectLog(fileId)
          tab.recalculateChunkListViewAndScrollToActiveElement()
        } else {
          logWarn(s"Not found: '${fileId.absolutePathAsString}'")
        }
      case None =>
        logorrrMain.selectLastLogFile()
    }
  }

  def init(stage: Stage, logorrrMain: LogoRRRMain): Unit = {

    val (width, height) = (LogoRRRGlobals.getStageWidth, LogoRRRGlobals.getStageHeight)

    val scene = new Scene(logorrrMain, width, height)

    /** only initialize accelerators after a scene is defined */
    LogoRRRAccelerators.initAccelerators(scene)
    // bind stage properties (they are initially set and constantly overwritten during execution)
    scene.windowProperty().addListener(MutStageSettings.windowListener)
    stage.sceneProperty().addListener(LogoRRRStage.sceneListener)
    stage.setTitle(AppInfo.fullAppName)
    stage.getIcons.add(LogoRRRStage.icon)
    stage.setScene(scene)
    stage.setOnCloseRequest((_: WindowEvent) => LogoRRRStage.shutdown(stage, logorrrMain))

    logorrrMain.init(stage)
  }

}


