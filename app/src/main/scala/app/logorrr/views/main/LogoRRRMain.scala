package app.logorrr.views.main

import app.logorrr.model.{DataModelEvent, DateFilterEvent, LogSource}
import app.logorrr.services.file.FileIdService
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

class LogoRRRMain(stage: Stage
                  , fileIdService: FileIdService
                  , isUnderTest: Boolean
                  , val logSource: LogSource) extends BorderPane with TinyLog:
  
  val bar = new MainMenuBar(stage
    , this
    , fileIdService
    , logSource.openFile
    , logSource.closeAllLogFiles()
    , isUnderTest)

  def init(): Unit =
    if !OsUtil.isMac || isUnderTest then setTop(bar)
    logSource.init(stage.getScene.getWindow)
    setCenter(logSource.getUi())

  def selectLastLogFile(): Unit = logSource.getUi().selectLastLogFile()

  def shutdown(): Unit = logSource.getUi().shutdown()

  addEventHandler(DataModelEvent.DateFilterEvent, (e: DateFilterEvent) => {
    logSource.getUi().applyTimeSettings(e.timeSettings)
    e.consume()
  })
