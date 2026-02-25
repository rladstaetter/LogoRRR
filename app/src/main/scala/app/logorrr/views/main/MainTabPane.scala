package app.logorrr.views.main

import app.logorrr.conf.{FileId, LogoRRRGlobals, TimeSettings}
import app.logorrr.model.{FileIdDividerSearchTerm, LogorrrModel, UiTarget}
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.{LogFileTab, TabControlEvent}
import javafx.beans.binding.Bindings
import javafx.scene.control.TabPane
import javafx.scene.control.{Tab, TabPane}
import net.ladstatt.util.log.TinyLog

import java.awt.Desktop
import java.util
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
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

  override def contains(p: FileId): Boolean = getLogFileTabs.exists(lr => lr.getFileId == p)

  override def selectLastLogFile(): Unit = getSelectionModel.selectLast()

  override def selectFile(fileId: FileId): Unit =
    getByFileId(fileId) match
      case Some(logFileTab) => getSelectionModel.select(logFileTab)
      case None => selectLastLogFile()

  /** Adds a new logfile to display and initializes bindings and listeners */
  override def addData(model: LogorrrModel): Unit =
    val tab = LogFileTab(getScene.getWindow, model)
    tab.init(getScene.getWindow, model.mutLogFileSettings)
    getTabs.add(tab)
    tab.initContextMenu()

  override def shutdown(): Unit =
    LogoRRRGlobals.mutSettings.someActiveLogProperty.unbind()
    styleProperty().unbind()
    idProperty().unbind()
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()

  def getSelectedTab: LogFileTab = getSelectionModel.getSelectedItem.asInstanceOf[LogFileTab]

  def applyTimeSettings(timeSettings: TimeSettings): Unit =
    getLogFileTabs.foreach(lf => lf.applyTimeSettings(timeSettings))

  // --- event handlers ---------------------------------------------------
  // close others action
  addEventHandler(TabControlEvent.CloseOthers, (e: TabControlEvent) => {
    val sourceTab = getSelectedTab
    // close all other items
    getTabs.stream.forEach {
      case tab: LogFileTab if !tab.isSelected => tab.shutdown()
      case _ =>
    }
    getTabs.retainAll(sourceTab) // throw away all but selected
    sourceTab.initContextMenu()
    e.consume()
  })

  addEventHandler(TabControlEvent.CloseAll, (e: TabControlEvent) => {
    getTabs.stream.forEach {
      case tab: LogFileTab => tab.shutdown()
      case _ =>
    }
    getTabs.clear()
    e.consume()
  })

  addEventHandler(TabControlEvent.CloseSelectedTab, (e: TabControlEvent) => {
    val sourceTab = getSelectedTab
    sourceTab.shutdown()
    getTabs.remove(sourceTab)
    e.consume()
  })

  addEventHandler(TabControlEvent.CloseLeft, (e: TabControlEvent) => {
    traverseAndRemove(getSelectedTab, getTabs.stream.iterator())
    e.consume()
  })

  addEventHandler(TabControlEvent.CloseRight, (e: TabControlEvent) => {
    traverseAndRemove(getSelectedTab, getTabs.reversed().stream.iterator())
    e.consume()
  })

  addEventHandler(TabControlEvent.OpenInFinder, (e: TabControlEvent) => {
    new Thread(() => Desktop.getDesktop.open(getSelectedTab.getFileId.asPath.getParent.toFile)).start()
    e.consume()
  })


  private def traverseAndRemove(selectedTab: LogFileTab, it: util.Iterator[Tab]): Unit = {
    var delete = true
    val lb = new util.ArrayList[Tab]()
    while (it.hasNext && delete) {
      val t = it.next()
      delete = !(t == selectedTab)
      t.asInstanceOf[LogFileTab].shutdown()
      if delete then lb.add(t)
    }
    getTabs.removeAll(lb)
    selectedTab.initContextMenu()
  }