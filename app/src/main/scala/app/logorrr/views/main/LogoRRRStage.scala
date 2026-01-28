package app.logorrr.views.main

import app.logorrr.LogoRRRApp
import app.logorrr.conf.mut.MutStageSettings
import app.logorrr.conf.{FileId, LogFileSettings, LogoRRRGlobals, SearchTerm}
import app.logorrr.model.{LogSource, LogorrrModel}
import app.logorrr.util.JfxUtils
import app.logorrr.views.LogoRRRAccelerators
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}
import net.ladstatt.util.log.TinyLog


object LogoRRRStage extends TinyLog:

  val icon: Image = new Image(getClass.getResourceAsStream("/logorrr-icon-32.png"))

  /** after scene got initialized and scene was set to stage immediately set position of stage */
  val sceneListener: ChangeListener[Scene] = JfxUtils.onNew[Scene](scene => {
    // x, y coordinates of upper left corner from last execution
    val (x, y) = (LogoRRRGlobals.getStageX, LogoRRRGlobals.getStageY)
    scene.getWindow.setX(x)
    scene.getWindow.setY(y)
  })

  def persistSettings(logorrrMain: LogoRRRMain): Unit =
    // current global state
    // following code can be removed if filters are bound to
    // global mutable Settings
    val settings = LogoRRRGlobals.getSettings

    // to save global filter state
    val activeSearchTerms: Map[FileId, (Seq[SearchTerm], Double)] =
      (for logFileTab <- logorrrMain.getLogFileTabs yield {
        logFileTab.getFileId -> (logFileTab.logPane.activeSearchTerms, logFileTab.logPane.getDividerPosition)
      }).toMap

    val updatedSettings: Map[String, LogFileSettings] =
      for (p, (sTerms, dPos)) <- activeSearchTerms yield
        p.absolutePathAsString -> settings.fileSettings(p.absolutePathAsString).copy(searchTerms = sTerms, dividerPosition = dPos)
    LogoRRRGlobals.persist(settings.copy(fileSettings = updatedSettings))

  def shutdown(stage: Stage, logorrrMain: LogoRRRMain): Unit =
    LogoRRRStage.persistSettings(logorrrMain)
    logorrrMain.shutdown()
    LogoRRRGlobals.unbindWindow()
    stage.getScene.windowProperty().removeListener(MutStageSettings.windowListener)
    stage.sceneProperty.removeListener(LogoRRRStage.sceneListener)
    logInfo(s"Stopped ${LogoRRRApp.appInfo.nameAndVersion}")

  def init(stage: Stage
           , logorrrMain: LogoRRRMain
           , someActiveFile: Option[FileId]
           , width: Int
           , height: Int): Unit =

    val scene = new Scene(logorrrMain, width, height)

    /** only initialize accelerators after a scene is defined */
    LogoRRRAccelerators.initAccelerators(scene)
    // bind stage properties (they are initially set and constantly overwritten during execution)
    scene.windowProperty().addListener(MutStageSettings.windowListener)
    stage.sceneProperty().addListener(LogoRRRStage.sceneListener)
    stage.setTitle(LogoRRRApp.appInfo.nameAndVersion)
    stage.getIcons.add(LogoRRRStage.icon)
    stage.setScene(scene)
    stage.setOnCloseRequest((_: WindowEvent) => {
      scene.getAccelerators.clear()
      LogoRRRStage.shutdown(stage, logorrrMain)
    })

    val logSource = new LogSource(logorrrMain.groups)

    val models: Seq[LogorrrModel] = logSource.loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings)

    logorrrMain.init(stage, models, someActiveFile)



