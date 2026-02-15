package app.logorrr.views.main

import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.model.{FileIdDividerSearchTerm, LogorrrModel, UiTarget}
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.LogFileTab
import javafx.beans.binding.Bindings
import javafx.scene.control.TabPane
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object MainTabPane:

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/drop-files-here.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: linear-gradient(to bottom, #f5f5dc, #d2b48c);
      |-fx-background-size: auto;
      |""".stripMargin


class MainTabPane extends TabPane with UiTarget with TinyLog:

  // -- bindings
  styleProperty().bind(Bindings.createStringBinding(() => MainTabPane.BackgroundStyle))
  idProperty().bind(Bindings.createStringBinding(() => UiNodes.MainTabPane.value))

  LogoRRRGlobals.mutSettings.someActiveLogProperty.bind(Bindings.createObjectBinding[Option[FileId]](() => {
    getSelectionModel.getSelectedItem match {
      case tab: LogFileTab => Option(tab.getFileId)
      case _ => None
    }
  }, getSelectionModel.selectedItemProperty()))

  private def getLogFileTabs: mutable.Seq[LogFileTab] =
    getTabs.asScala.flatMap:
      case l: LogFileTab => Option(l)
      case _ => None

  private def getByFileId(fileId: FileId): Option[LogFileTab] = getLogFileTabs.find(_.getFileId == fileId)

  override def getInfos: Seq[FileIdDividerSearchTerm] = getLogFileTabs.map(_.getInfo).toSeq

  override def selectLastLogFile(): Unit = getSelectionModel.selectLast()

  override def selectFile(fileId: FileId): Unit =
    getByFileId(fileId) match
      case Some(logFileTab) => getSelectionModel.select(logFileTab)
      case None => selectLastLogFile()

  override def contains(p: FileId): Boolean = getLogFileTabs.exists(lr => lr.getFileId == p)

  /** Adds a new logfile to display and initializes bindings and listeners */
  override def addData(model: LogorrrModel): Unit =
    val tab =  LogFileTab(model)
    tab.init(getScene.getWindow, model.mutLogFileSettings)
    getTabs.add(tab)
    tab.initContextMenu()

  override def shutdown(): Unit =
    LogoRRRGlobals.mutSettings.someActiveLogProperty.unbind()
    styleProperty().unbind()
    idProperty().unbind()
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()

