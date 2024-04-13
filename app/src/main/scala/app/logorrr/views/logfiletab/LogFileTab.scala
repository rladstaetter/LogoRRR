package app.logorrr.views.logfiletab

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.util._
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.logfiletab.actions._
import app.logorrr.views.search.Fltr
import app.logorrr.views.{LogoRRRAccelerators, UiNode, UiNodeAware}
import javafx.beans.binding.Bindings
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.event.Event
import javafx.scene.control._

import java.lang
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object LogFileTab extends UiNodeAware {

  /** background for file log tabs */
  private val BackgroundStyle: String =
    """|-fx-background-color: white;
       |-fx-border-width: 1px 1px 1px 0px;
       |-fx-border-color: lightgrey""".stripMargin

  /** background selected for file log tabs */
  private val BackgroundSelectedStyle: String =
    """|-fx-background-color: floralwhite;
       |-fx-border-width: 1px 1px 1px 0px;
       |-fx-border-color: lightgrey""".stripMargin

  /** background for zipfile log tabs */
  private val ZipBackgroundStyle: String =
    """|-fx-background-color: white;
       |-fx-border-width: 1px 1px 1px 0px;
       |-fx-border-color: lightgrey""".stripMargin

  /** background selected for file log tabs */
  private val ZipBackgroundSelectedStyle: String =
    """|-fx-background-color: floralwhite;
       |-fx-border-width: 1px 1px 1px 0px;
       |-fx-border-color: lightgrey""".stripMargin

  def apply(mutLogFileSettings: MutLogFileSettings
            , entries: ObservableList[LogEntry]): LogFileTab = {
    new LogFileTab(mutLogFileSettings.getFileId
      , mutLogFileSettings
      , entries)
  }

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogFileTab])

}

/**
 * Represents a single 'document' UI approach for a log file.
 *
 * One can view / interact with more than one log file at a time, using tabs here feels quite natural.
 *
 * @param entries report instance holding information of log file to be analyzed
 * */
class LogFileTab(val fileId: FileId
                 , val mutLogFileSettings: MutLogFileSettings
                 , val entries: ObservableList[LogEntry]) extends Tab
  with TimerCode
  with CanLog {

  setId(LogFileTab.uiNode(fileId).value)

  if (fileId.isZipEntry) {
    setStyle(LogFileTab.ZipBackgroundStyle)
  } else {
    setStyle(LogFileTab.BackgroundStyle)
  }

  assert(fileId == mutLogFileSettings.getFileId)

  private lazy val logTailer = LogTailer(fileId, entries)

  val logFileTabContent = new LogFileTabContent(mutLogFileSettings, entries)

  private def startTailer(): Unit = {
    logFileTabContent.addTailerListener()
    logTailer.start()
  }

  private def stopTailer(): Unit = {
    logFileTabContent.removeTailerListener()
    logTailer.stop()
  }

  private val autoScrollListener = JfxUtils.onNew[lang.Boolean] {
    b =>
      if (b) {
        startTailer()
      } else {
        stopTailer()
      }
  }

  private val filterChangeListener = {
    def handleFilterChange(change: ListChangeListener.Change[_ <: Fltr]): Unit = {
      while (change.next()) {
        Future {
          LogoRRRGlobals.persist()
        }
      }
    }

    JfxUtils.mkListChangeListener(handleFilterChange)
  }

  private val selectedListener = JfxUtils.onNew[lang.Boolean](b => {
    if (b) {
      if (fileId.isZipEntry) {
        setStyle(LogFileTab.ZipBackgroundSelectedStyle)
      } else {
        setStyle(LogFileTab.BackgroundSelectedStyle)
      }

      /* change active text field depending on visible tab */
      LogoRRRAccelerators.setActiveSearchTextField(logFileTabContent.opsToolBar.searchTextField)
      LogoRRRAccelerators.setActiveRegexToggleButton(logFileTabContent.opsToolBar.regexToggleButton)
      recalculateChunkListViewAndScrollToActiveElement()
    } else {
      if (fileId.isZipEntry) {
        setStyle(LogFileTab.ZipBackgroundStyle)
      } else {
        setStyle(LogFileTab.BackgroundStyle)
      }
    }
  })


  def recalculateChunkListViewAndScrollToActiveElement(): Unit = {
    logFileTabContent.recalculateChunkListView()
    logFileTabContent.scrollToActiveElement()
  }

  def init(): Unit = timeR({
    setTooltip(new LogFileTabToolTip(fileId, entries))

    initBindings()

    addListeners()

    logFileTabContent.init()

    /** don't monitor file anymore if tab is closed, free listeners */
    setOnCloseRequest((_: Event) => shutdown())

    if (mutLogFileSettings.isAutoScrollActive) {
      startTailer()
    }

    /** top component for log view */
    setContent(logFileTabContent)

    // we have to set the context menu in relation to the visibility of the tab itself
    // otherwise those context menu actions can be performed without selecting the tab
    // which is kind of confusing
    selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean] {
      case java.lang.Boolean.TRUE =>
        // getTabPane can be null on initialisation
        Option(getTabPane).foreach(_ => setContextMenu(mkContextMenu()))
      case java.lang.Boolean.FALSE => setContextMenu(null)
    })

  }, s"Init '${fileId.value}'")

  def initContextMenu(): Unit = setContextMenu(mkContextMenu())


  private def mkContextMenu(): ContextMenu = {
    val closeMenuItem = new CloseTabMenuItem(fileId, this)
    val openInFinderMenuItem = new OpenInFinderMenuItem(fileId)

    val closeOtherFilesMenuItem = new CloseOtherFilesMenuItem(fileId, this)
    val closeAllFilesMenuItem = new CloseAllFilesMenuItem(fileId, this)

    // close left/right is not always shown. see https://github.com/rladstaetter/LogoRRR/issues/159
    val leftRightCloser =
      if (getTabPane.getTabs.size() == 1) {
        Seq()
        // current tab is the first one, show only 'right'
      } else if (getTabPane.getTabs.indexOf(this) == 0) {
        Seq(new CloseRightFilesMenuItem(fileId, this))
        // we are at the end of the list
      } else if (getTabPane.getTabs.indexOf(this) == getTabPane.getTabs.size - 1) {
        Seq(new CloseLeftFilesMenuItem(fileId, this))
        // we are somewhere in between, show both options
      } else {
        Seq(new CloseLeftFilesMenuItem(fileId, this), new CloseRightFilesMenuItem(fileId, this))
      }


    val items = {
      // special handling if there is only one tab
      if (getTabPane.getTabs.size() == 1) {
        Seq(closeMenuItem, openInFinderMenuItem)
      } else {
        Seq(closeMenuItem
          , closeOtherFilesMenuItem
          , closeAllFilesMenuItem) ++ leftRightCloser ++ Seq(openInFinderMenuItem)
      }
    }

    new ContextMenu(items: _*)
  }

  private def addListeners(): Unit = {
    selectedProperty().addListener(selectedListener)

    mutLogFileSettings.autoScrollActiveProperty.addListener(autoScrollListener)
    mutLogFileSettings.filtersProperty.addListener(filterChangeListener)
  }

  private def initBindings(): Unit = {
    if (fileId.isZipEntry) {
      textProperty.bind(Bindings.concat(fileId.zipEntryPath))
    } else {
      textProperty.bind(Bindings.concat(fileId.fileName))
    }
  }


  private def removeListeners(): Unit = {
    selectedProperty().removeListener(selectedListener)

    logFileTabContent.removeListeners()
    mutLogFileSettings.autoScrollActiveProperty.removeListener(autoScrollListener)
    mutLogFileSettings.filtersProperty.removeListener(filterChangeListener)
  }

  def shutdown(): Unit = {
    if (mutLogFileSettings.isAutoScrollActive) {
      stopTailer()
    }
    removeListeners()
    LogoRRRGlobals.removeLogFile(fileId)
  }


}


