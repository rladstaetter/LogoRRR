package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm}
import app.logorrr.model.{FileIdDividerSearchTerm, LogEntry, LogorrrModel, UiTarget}
import app.logorrr.util.*
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.block.BlockConstants
import app.logorrr.views.ops.*
import app.logorrr.views.search.st.SearchTermToolBar
import app.logorrr.views.search.{MutableSearchTerm, OpsToolBar}
import app.logorrr.views.text.LogTextView
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Window
import javafx.util.Subscription

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LogFilePane(mutLogFileSettings: MutLogFileSettings
                  , val entries: ObservableList[LogEntry]) extends BorderPane with UiTarget:

  setStyle("-fx-background-color: white;")

  /** provides a tool to observe log files */
  private val logTailer = new LogTailer

  val autoScrollActiveProperty = new SimpleBooleanProperty()

  def getFileId: FileId = mutLogFileSettings.getFileId


  /** if a search term is added or removed this will be saved to disc */
  private val searchTermChangeListener: ListChangeListener[MutableSearchTerm] =
    def handleSearchTermChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit = {
      while change.next() do
        Future(LogoRRRGlobals.persist(LogoRRRGlobals.getSettings))
    }

    JfxUtils.mkListChangeListener[MutableSearchTerm](handleSearchTermChange)

  /** if autoscroll checkbox is enabled, monitor given log file for changes. if there are any, this will be reflected in the ui */
  private val autoScrollSubscription: Subscription =
    autoScrollActiveProperty.subscribe((old: java.lang.Boolean, newVal: java.lang.Boolean) => enableAutoscroll(newVal))


  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredEntries = new FilteredList[LogEntry](entries)

  // display text to the right
  private val logTextView = new LogTextView(filteredEntries)

  // graphical display to the left
  private val chunkListView = LogoRRRChunkListView(mutLogFileSettings, filteredEntries, logTextView.scrollToItem, widthProperty)

  val opsToolBar = new OpsToolBar(mutLogFileSettings, chunkListView, entries, filteredEntries)

  private val searchTermToolBar = new SearchTermToolBar(mutLogFileSettings, filteredEntries)

  val textSizeSlider = new TextSizeSlider
  val blockSizeSlider = new BlockSizeSlider

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

  def activeSearchTerms: Seq[SearchTerm] = searchTermToolBar.activeSearchTerms()

  def enableAutoscroll(enabled: Boolean): Unit =
    if enabled then
      filteredEntries.addListener(autoScrollEventListener)
      logTailer.start()
    else
      filteredEntries.removeListener(autoScrollEventListener)
      logTailer.stop()


  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, searchTermToolBar)

  private val pane = new SplitPane(chunkPane, textPane)

  def init(window: Window): Unit =

    searchTermToolBar.init()
    opsToolBar.init(window
      , mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.autoScrollActiveProperty
      , mutLogFileSettings.mutSearchTerms
      , filteredEntries
    )
    textPane.init(mutLogFileSettings.fileIdProperty, mutLogFileSettings.fontSizeProperty)
    chunkPane.init(mutLogFileSettings.fileIdProperty, mutLogFileSettings.blockSizeProperty)
    textSizeSlider.bind(mutLogFileSettings.fileIdProperty)
    blockSizeSlider.bind(mutLogFileSettings.fileIdProperty)
    autoScrollActiveProperty.bind(mutLogFileSettings.autoScrollActiveProperty)

    logTailer.init(mutLogFileSettings.fileIdProperty, entries)
    divider.setPosition(mutLogFileSettings.getDividerPosition)
    setTop(opsRegion)
    setCenter(pane)
    logTextView.init(mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.fontSizeProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , mutLogFileSettings.mutSearchTerms
    )
    chunkListView.init()
    enableAutoscroll(mutLogFileSettings.isAutoScrollActive)
    mutLogFileSettings.mutSearchTerms.addListener(searchTermChangeListener)

  private def divider: SplitPane.Divider = pane.getDividers.get(0)

  def getDividerPosition: Double = divider.getPosition

  def shutdown(): Unit =
    searchTermToolBar.shutdown()
    opsToolBar.shutdown(mutLogFileSettings.autoScrollActiveProperty, mutLogFileSettings.mutSearchTerms, filteredEntries)
    textPane.shutdown(mutLogFileSettings.fontSizeProperty)
    chunkPane.shutdown(mutLogFileSettings.blockSizeProperty)
    textSizeSlider.unbind()
    blockSizeSlider.unbind()
    autoScrollActiveProperty.unbind()
    logTailer.shutdown(mutLogFileSettings.fileIdProperty, entries)
    if mutLogFileSettings.isAutoScrollActive then enableAutoscroll(false)
    logTextView.shutdown(
      mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , mutLogFileSettings.mutSearchTerms)
    chunkListView.shutdown()
    autoScrollSubscription.unsubscribe()
    mutLogFileSettings.mutSearchTerms.removeListener(searchTermChangeListener)
    LogoRRRGlobals.removeLogFile(getFileId)

  override def addData(model: LogorrrModel): Unit = () // TOOD IMPLEMENT ME

  override def contains(fileId: FileId): Boolean = fileId.equals(getFileId)

  override def selectFile(fileId: FileId): Unit = ()

  override def selectLastLogFile(): Unit = ()

  override def getInfos: Seq[FileIdDividerSearchTerm] = Seq(FileIdDividerSearchTerm(getFileId, activeSearchTerms, getDividerPosition))




