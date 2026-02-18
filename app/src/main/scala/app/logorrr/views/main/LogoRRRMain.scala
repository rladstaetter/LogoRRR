package app.logorrr.views.main

import app.logorrr.model.LogSource
import app.logorrr.services.file.FileIdService
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

class LogoRRRMain(stage: Stage
                  , fileIdService: FileIdService
                  , isUnderTest: Boolean
                  , val logSource: LogSource) extends BorderPane with TinyLog:

  val bar = new MainMenuBar(stage, fileIdService, logSource.openFile, logSource.closeAllLogFiles(), isUnderTest)

  def init(): Unit =
    if !OsUtil.isMac || isUnderTest then setTop(bar)
    setCenter(logSource.ui)
    logSource.loadLogFiles()

  def selectLastLogFile(): Unit = logSource.ui.selectLastLogFile()

  def shutdown(): Unit = logSource.ui.shutdown()

