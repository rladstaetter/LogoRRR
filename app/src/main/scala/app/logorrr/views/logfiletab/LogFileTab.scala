package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{FileIdDividerSearchTerm, FileIdPropertyHolder, LogEntry, LogorrrModel}
import app.logorrr.views.LogoRRRAccelerators
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.event.Event
import javafx.scene.control.*
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog

import java.util.function.Consumer


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
    """|-fx-background-color: blanchedalmond;
       |-fx-border-width: 1px 1px 1px 0px;
       |-fx-border-color: lightgrey""".stripMargin

  /** background selected for file log tabs */
  private val ZipBackgroundSelectedStyle: String =
    """|-fx-background-color: burlywood;
       |-fx-border-width: 1px 1px 1px 0px;
       |-fx-border-color: lightgrey""".stripMargin

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogFileTab])

  def apply(window: Window, model: LogorrrModel): LogFileTab =
    new LogFileTab(window, model.mutLogFileSettings, model.entries)

/**
 * Represents a single 'document' UI approach for a log file.
 *
 * One can view / interact with more than one log file at a time, using tabs here feels quite natural.
 *
 * @param mutLogFileSettings settings for given log file
 * @param entries            report instance holding information of log file to be analyzed
 * */
class LogFileTab(owner: Window, mutLogFileSettings: MutLogFileSettings, entries: ObservableList[LogEntry])
  extends Tab
    with FileIdPropertyHolder
    with TinyLog:

  private val selectedSubscription =
    selectedProperty.subscribe(new Consumer[java.lang.Boolean] {
      def accept(newVal: java.lang.Boolean): Unit =
        if newVal then {
          initContextMenu()
          LogoRRRAccelerators.setActiveSearchTextField(logPane.opsToolBar.searchRegion.searchTextField)
        } else {
          setContextMenu(null)
        }
    })

  val logPane = new LogFilePane(owner, mutLogFileSettings, entries)
  val logFileTabToolTip = new LogFileTabToolTip

  def init(window: Window
           , mutLogFileSettings: MutLogFileSettings): Unit =
    bindFileIdProperty(mutLogFileSettings.fileIdProperty)
    idProperty().bind(Bindings.createStringBinding(() => {
      LogFileTab.uiNode(fileIdProperty.get).value
    }, fileIdProperty))


    // set style depending on type and selected status
    styleProperty().bind(Bindings.createStringBinding(() => {
      (getFileId.isZipEntry, isSelected) match {
        case (true, true) => LogFileTab.ZipBackgroundSelectedStyle
        case (true, false) => LogFileTab.ZipBackgroundStyle
        case (false, true) => LogFileTab.BackgroundSelectedStyle
        case (false, false) => LogFileTab.BackgroundStyle
      }
    }, mutLogFileSettings.fileIdProperty, selectedProperty()))

    textProperty().bind(Bindings.createStringBinding(
      () => if getFileId.isZipEntry then getFileId.zipEntryPath else getFileId.fileName
      , mutLogFileSettings.fileIdProperty))
    // setup bindings end ----

    logFileTabToolTip.init(mutLogFileSettings.fileIdProperty, entries)


    setTooltip(logFileTabToolTip)
    logPane.init(window, mutLogFileSettings.fileIdProperty)
    setContent(logPane)
    setOnCloseRequest((_: Event) => shutdown())


  def initContextMenu(): Unit = {
    Option(getTabPane) match {
      case Some(value) => setContextMenu(new LogFileTabContextMenu(getFileId, value, this))
      case None =>
    }
  }

  def shutdown(): Unit =
    logFileTabToolTip.unbind()

    // disable autoscroll
    // unbind bindings
    idProperty().unbind()
    textProperty().unbind()
    styleProperty().unbind()

    // remove subscriptions
    selectedSubscription.unsubscribe()
    // shutdown pane
    logPane.shutdown()
    idProperty.unbind()
    unbindFileIdProperty()

  def getInfo = FileIdDividerSearchTerm(getFileId, logPane.activeSearchTerms, logPane.getDividerPosition)



