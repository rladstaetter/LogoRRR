package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.{LogFilePredicate, MutLogFileSettings}
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm, TimeSettings}
import app.logorrr.model.*
import app.logorrr.util.*
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.block.BlockConstants
import app.logorrr.views.ops.*
import app.logorrr.views.search.st.SearchTermToolBar
import app.logorrr.views.search.{OpsToolBar, SearchTextField}
import app.logorrr.views.text.LogTextView
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.beans.property.{ObjectPropertyBase, SimpleBooleanProperty, SimpleLongProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Window
import javafx.util.Subscription
import net.ladstatt.util.log.TinyLog

import scala.compiletime.uninitialized

object LogFilePane extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogFilePane])


class LogFilePane(owner: Window
                  , mutLogFileSettings: MutLogFileSettings
                  , val entries: ObservableList[LogEntry])
  extends BorderPane with UiTarget
    with FileIdPropertyHolder
    with TinyLog
    with BoundId(LogFilePane.uiNode(_).value):

  setStyle("-fx-background-color: white;")
  /** provides a tool to observe log files */
  private val logTailer = new LogTailer

  private val autoScrollActiveProperty = new SimpleBooleanProperty()

  /** if a search term is added or removed this will be saved to disc */


  /** if autoscroll checkbox is enabled, monitor given log file for changes. if there are any, this will be reflected in the ui */
  private var autoScrollSubscription: Subscription = uninitialized

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredEntries = new FilteredList[LogEntry](entries, mutLogFileSettings.showPredicate)

  // display text to the right
  private val logTextView = new LogTextView(filteredEntries)

  // graphical display to the left
  private val chunkListView: LogoRRRChunkListView = LogoRRRChunkListView(mutLogFileSettings, filteredEntries, logTextView.scrollToItem, widthProperty)

  val opsToolBar = new OpsToolBar(owner, this, mutLogFileSettings, chunkListView, entries)

  private val searchTermToolBar = new SearchTermToolBar(mutLogFileSettings, entries)

  private val textSizeSlider = new TextSizeSlider

  private val blockSizeSlider = new BlockSizeSlider

  private val textPane: LogPartPane = new LogPartPane(
    logTextView
    , textSizeSlider
    , PaneDefinition(prop => IncreaseTextSizeButton.uiNode(prop.get).value, TextSizeButton.mkLabel(12), TextConstants.fontSizeStep, TextConstants.MaxFontSize)
    , PaneDefinition(prop => DecreaseTextSizeButton.uiNode(prop.get).value, TextSizeButton.mkLabel(8), TextConstants.fontSizeStep, TextConstants.MinFontSize)
  )

  private val chunkPane: LogPartPane = new LogPartPane(
    chunkListView
    , blockSizeSlider
    , PaneDefinition(prop => IncreaseBlockSizeButton.uiNode(prop.get).value, RectButton.mkR(IncreaseBlockSizeButton.Size, IncreaseBlockSizeButton.Size, Color.GRAY), BlockConstants.BlockSizeStep, BlockConstants.MaxBlockSize)
    , PaneDefinition(prop => DecreaseBlockSizeButton.uiNode(prop.get).value, RectButton.mkR(DecreaseBlockSizeButton.Size, DecreaseBlockSizeButton.Size, Color.GRAY), BlockConstants.BlockSizeStep, BlockConstants.MinBlockSize)
  )


  // if active scroll automatically to the end of the list
  private lazy val autoScrollEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  def enableFollowMode(logFilePositionProperty: SimpleLongProperty): Unit =
    filteredEntries.addListener(autoScrollEventListener)
    logTailer.start(logFilePositionProperty.get())

  def disableFollowMode(logFilePositionProperty: SimpleLongProperty): Unit =
    filteredEntries.removeListener(autoScrollEventListener)
    logTailer.stop()
    // calculating length of file to be sure, don't trust logTailer
    logFilePositionProperty.set(fileIdProperty.get().asPath.toFile.length())


  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, searchTermToolBar)

  private val pane = new SplitPane(chunkPane, textPane)

  val searchTextField: SearchTextField = opsToolBar.searchTextField

  def init(window: Window
           , fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    bindFileIdProperty(fileIdProperty)
    bindIdProperty(fileIdProperty)
    searchTermToolBar.init(window)
    opsToolBar.init(
      fileIdProperty
      , mutLogFileSettings.autoScrollActiveProperty
      , mutLogFileSettings.mutSearchTerms
      , filteredEntries
    )
    textPane.init(fileIdProperty, mutLogFileSettings.fontSizeProperty)
    chunkPane.init(fileIdProperty, mutLogFileSettings.blockSizeProperty)
    textSizeSlider.bindIdProperty(fileIdProperty)
    blockSizeSlider.bindIdProperty(fileIdProperty)
    logTailer.init(fileIdProperty, entries)
    autoScrollSubscription = autoScrollActiveProperty.subscribe(
      (old: java.lang.Boolean, newVal: java.lang.Boolean) =>
        if newVal then
          enableFollowMode(mutLogFileSettings.logFilePositionProperty)
        else
          disableFollowMode(mutLogFileSettings.logFilePositionProperty))

    autoScrollActiveProperty.bind(mutLogFileSettings.autoScrollActiveProperty)
    pane.getDividers.get(0).positionProperty().bindBidirectional(mutLogFileSettings.dividerPositionProperty)
    setTop(opsRegion)
    setCenter(pane)
    logTextView.init(fileIdProperty
      , mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.fontSizeProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , mutLogFileSettings.mutSearchTerms
    )
    chunkListView.init(fileIdProperty)

  private def divider: SplitPane.Divider = pane.getDividers.get(0)

  def getDividerPosition: Double = {
    divider.getPosition
  }

  def shutdown(): Unit =
    searchTermToolBar.shutdown()
    opsToolBar.shutdown(mutLogFileSettings.autoScrollActiveProperty, mutLogFileSettings.mutSearchTerms, filteredEntries)
    textPane.shutdown(mutLogFileSettings.fontSizeProperty)
    chunkPane.shutdown(mutLogFileSettings.blockSizeProperty)
    textSizeSlider.unbindIdProperty()
    blockSizeSlider.unbindIdProperty()
    autoScrollActiveProperty.unbind()
    pane.getDividers.get(0).positionProperty().unbindBidirectional(mutLogFileSettings.dividerPositionProperty)
    logTailer.shutdown(fileIdProperty, entries)
    disableFollowMode(mutLogFileSettings.logFilePositionProperty)
    logTextView.shutdown(
      mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , mutLogFileSettings.mutSearchTerms)
    chunkListView.shutdown()
    autoScrollSubscription.unsubscribe()
    LogoRRRGlobals.removeLogFile(fileIdProperty.get())
    unbindFileIdProperty()
    unbindIdProperty()

  override def addData(model: LogorrrModel): Unit = () // TOOD IMPLEMENT ME

  override def contains(fileId: FileId): Boolean = fileId.equals(fileIdProperty.get())

  override def selectFile(fileId: FileId): Unit = ()

  override def selectLastLogFile(): Unit = ()

  def applyTimeSettings(timeSettings: TimeSettings): Unit =
    TimeUtil.calculate(mutLogFileSettings, chunkListView, entries, timeSettings, opsToolBar.timestampSettingsRegion)

  private def updateDisplay(): Unit = {
    mutLogFileSettings.repaintProperty.set(!mutLogFileSettings.repaintProperty.get())
    LogFilePredicate.update(filteredEntries.predicateProperty(), mutLogFileSettings.showPredicate)
  }

  // ---- EVENT HANDLER DEFINITIONS
  // sets filter directly from local timestamp settings dialog (as opposed to the global settings dialog)
  addEventHandler(DataModelEvent.DateFilterEvent, (e: DateFilterEvent) => {
    applyTimeSettings(e.timeSettings)
    e.consume()
  })

  addEventHandler(DataModelEvent.RemoveDateFilterEvent, (e: RemoveDateFilterEvent) => timeR({
    mutLogFileSettings.setTimeSettings(TimeSettings.Invalid)
    // we have to deactivate this listener otherwise
    chunkListView.removeInvalidationListener()
    val tempList = new java.util.ArrayList[LogEntry]()
    entries.forEach(e => {
      tempList.add(e.withOutTimestamp())
    })
    entries.setAll(tempList)
    // activate listener again
    chunkListView.addInvalidationListener()
    updateDisplay()
    e.consume()
  }, "updateList"))

  addEventHandler(DataModelEvent.AddSearchTermButtonEvent, (e: AddSearchTermButtonEvent) => {
    updateDisplay()
    e.consume()
  })

  addEventHandler(DataModelEvent.RemoveSearchTermButtonEvent, (e: RemoveSearchTermButtonEvent) => {
    mutLogFileSettings.mutSearchTerms.remove(e.mutableSearchTerm)
    updateDisplay()
    e.consume()
  })

  addEventHandler(DataModelEvent.UpdateLogFilePredicate, (e: UpdateLogFilePredicate) =>
    updateDisplay()
    e.consume()
  )

