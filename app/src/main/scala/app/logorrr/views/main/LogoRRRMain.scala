package app.logorrr.views.main

import app.logorrr.conf.{FileId, LogFileSettings, LogoRRRGlobals}
import app.logorrr.model.{FileIdDividerSearchTerm, LogSource, LogorrrModel}
import app.logorrr.services.file.FileIdService
import app.logorrr.util.JfxUtils
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog

class LogoRRRMain(stage: Stage
                  , fileIdService: FileIdService
                  , isUnderTest: Boolean
                  , val logSource: LogSource) extends BorderPane with TinyLog:

  val bar = new MainMenuBar(stage, fileIdService, logSource.openFile, logSource.closeAllLogFiles(), isUnderTest)

  def init(): Unit =
    setTop(bar)
    setCenter(logSource.ui)
    logSource.loadLogFiles()

  def selectLastLogFile(): Unit = logSource.ui.selectLastLogFile()

  def shutdown(): Unit = logSource.ui.shutdown()

