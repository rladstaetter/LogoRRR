package app.logorrr.views.main

import app.logorrr.conf.{DefaultSearchTermGroups, FileId, LogoRRRGlobals}
import app.logorrr.io.IoManager
import app.logorrr.model.LogorrrModel
import app.logorrr.services.file.FileIdService
import app.logorrr.util.JfxUtils
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog

import scala.collection.mutable

class LogoRRRMain(stage: Stage
                  , fileIdService: FileIdService
                  , isUnderTest: Boolean
                  , val groups: DefaultSearchTermGroups) extends BorderPane with TinyLog:

  val bar = new MainMenuBar(stage, fileIdService, openFile, closeAllLogFiles(), isUnderTest)

  private val mainTabPane = new MainTabPane(groups)

  def init(stage: Stage
           , models: Seq[LogorrrModel]
           , someActiveFile: Option[FileId]): Unit =
    setTop(bar)
    setCenter(mainTabPane)

    JfxUtils.execOnUiThread:
      // important to execute this code on jfx thread
      // don't change the ordering of following statements ;-)
      models.foreach(t => mainTabPane.addData(LogorrrModel(t.mutLogFileSettings, t.entries)))
      someActiveFile match {
        case Some(value) if contains(value) => mainTabPane.selectFile(value)
        case _ => selectLastLogFile()
      }
      stage.show()
      stage.toFront()

  /** called when 'Open File' from the main menu bar is selected. */
  def openFile(fileId: FileId): Unit = {
    if IoManager.isZip(fileId.asPath) then
      mainTabPane.openZipFile(fileId.asPath, groups)
    else if contains(fileId) then
      mainTabPane.selectFile(fileId)
    else
      mainTabPane.addFile(fileId, groups)
  }

  /**
   * Removes all log files and clears settings
   **/
  private def closeAllLogFiles(): Unit =
    shutdown()
    LogoRRRGlobals.clearLogFileSettings()

  def contains(fileId: FileId): Boolean = mainTabPane.contains(fileId)

  def selectLastLogFile(): Unit = mainTabPane.selectLastLogFile()

  def getLogFileTabs: mutable.Seq[LogFileTab] = mainTabPane.getLogFileTabs

  def shutdown(): Unit = mainTabPane.shutdown()

