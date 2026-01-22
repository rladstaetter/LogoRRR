package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.model.LogEntry
import app.logorrr.util.*
import app.logorrr.views.LogoRRRAccelerators
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.actions.*
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.Bindings
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.event.Event
import javafx.scene.control.*
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

import java.lang
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object LogFileTab extends UiNodeFileIdAware:

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
            , entries: ObservableList[LogEntry]): LogFileTab =
    new LogFileTab(mutLogFileSettings.getFileId
      , mutLogFileSettings
      , entries)

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogFileTab])


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
  with TimerCode with TinyLog:

  setId(LogFileTab.uiNode(fileId).value)

  if fileId.isZipEntry then
    setStyle(LogFileTab.ZipBackgroundStyle)
  else
    setStyle(LogFileTab.BackgroundStyle)

  assert(fileId == mutLogFileSettings.getFileId)

  val logFileTabContent = new LogFileTabContent(mutLogFileSettings, entries)

  /** start / stop tailer */
  private val autoScrollListener = JfxUtils.onNew[lang.Boolean](b => logFileTabContent.enableAutoscroll(b))

  private val searchTermChangeListener: ListChangeListener[MutableSearchTerm] =

    def handleSearchTermChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit =
      while change.next() do
        Future(LogoRRRGlobals.persist(LogoRRRGlobals.getSettings))

    JfxUtils.mkListChangeListener[MutableSearchTerm](handleSearchTermChange)

  private val selectedListener = JfxUtils.onNew[lang.Boolean](b => {
    if b then {
      if fileId.isZipEntry then {
        setStyle(LogFileTab.ZipBackgroundSelectedStyle)
      } else {
        setStyle(LogFileTab.BackgroundSelectedStyle)
      }

      /* change active text field depending on visible tab */
      LogoRRRAccelerators.setActiveSearchTextField(logFileTabContent.opsToolBar.searchTextField)
      recalculateChunkListViewAndScrollToActiveElement()
    } else {
      if fileId.isZipEntry then {
        setStyle(LogFileTab.ZipBackgroundStyle)
      } else {
        setStyle(LogFileTab.BackgroundStyle)
      }
    }
  })


  def recalculateChunkListViewAndScrollToActiveElement(): Unit =
    logFileTabContent.recalculateChunkListView()
    logFileTabContent.scrollToActiveElement()

  def init(): Unit = timeR({
    setTooltip(new LogFileTabToolTip(fileId, entries))
    initBindings()
    addListeners()
    logFileTabContent.init()

    /** don't monitor file anymore if tab is closed, free listeners */
    setOnCloseRequest((_: Event) => shutdown())

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

  private def mkContextMenu(): ContextMenu =
    val closeMenuItem = new CloseTabMenuItem(fileId, this)
    val openInFinderMenuItem = new OpenInFinderMenuItem(fileId)

    val closeOtherFilesMenuItem = new CloseOtherFilesMenuItem(fileId, this)
    val closeAllFilesMenuItem = new CloseAllFilesMenuItem(fileId, this)

    // close left/right is not always shown. see https://github.com/rladstaetter/LogoRRR/issues/159
    val leftRightCloser =
      if getTabPane.getTabs.size() == 1 then
        Seq()
      // current tab is the first one, show only 'right'
      else if getTabPane.getTabs.indexOf(this) == 0 then
        Seq(new CloseRightFilesMenuItem(fileId, this))
      // we are at the end of the list
      else if getTabPane.getTabs.indexOf(this) == getTabPane.getTabs.size - 1 then
        Seq(new CloseLeftFilesMenuItem(fileId, this))
      // we are somewhere in between, show both options
      else
        Seq(new CloseLeftFilesMenuItem(fileId, this), new CloseRightFilesMenuItem(fileId, this))

    // val mergeTimedMenuItem = new MergeTimedMenuItem(fileId, this.getTabPane.asInstanceOf[MainTabPane])

    val items: Seq[MenuItem] =
      // special handling if there is only one tab
      if getTabPane.getTabs.size() == 1 then
        if OsUtil.isMac then
          Seq(closeMenuItem)
        else
          Seq(closeMenuItem, openInFinderMenuItem)
      else
        Seq(closeMenuItem
          , closeOtherFilesMenuItem
          , closeAllFilesMenuItem) ++ leftRightCloser ++ {
          if OsUtil.isMac then
            Seq()
          else
            Seq(openInFinderMenuItem)
        }
    new ContextMenu(items *)

  private def addListeners(): Unit =
    selectedProperty().addListener(selectedListener)

    mutLogFileSettings.autoScrollActiveProperty.addListener(autoScrollListener)
    mutLogFileSettings.mutSearchTerms.addListener(searchTermChangeListener)

  private def initBindings(): Unit =
    if fileId.isZipEntry then
      textProperty.bind(Bindings.concat(fileId.zipEntryPath))
    else
      textProperty.bind(Bindings.concat(fileId.fileName))


  private def removeListeners(): Unit =
    selectedProperty().removeListener(selectedListener)

    logFileTabContent.removeListeners()
    mutLogFileSettings.autoScrollActiveProperty.removeListener(autoScrollListener)
    mutLogFileSettings.mutSearchTerms.removeListener(searchTermChangeListener)

  def shutdown(): Unit =
    if mutLogFileSettings.isAutoScrollActive then
      logFileTabContent.enableAutoscroll(false)
    removeListeners()
    LogoRRRGlobals.removeLogFile(fileId)




