package app.logorrr.views.logfiletab

import app.logorrr.conf.{LogoRRRGlobals, SearchTerm}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.*
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.block.BlockConstants
import app.logorrr.views.ops.*
import app.logorrr.views.search.{MutableSearchTerm, OpsToolBar}
import app.logorrr.views.search.st.SearchTermToolBar
import app.logorrr.views.text.LogTextView
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.beans.property.Property
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.{ListView, Slider, SplitPane}
import javafx.scene.layout.{BorderPane, HBox, Priority, VBox}
import javafx.scene.paint.Color

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PaneDefinition(jfxId: String, graphic: Node, step: Int, boundary: Int)

object LogFilePane:

  /** wire pane and slider together */
  private def mkPane(listView: ListView[?]
                     , slider: Slider
                     , inc: PaneDefinition
                     , dec: PaneDefinition
                     , boundProp: Property[Number]): BorderPane =
    val increaseButton = IncreaseSizeButton(inc.jfxId, inc.graphic, inc.step, inc.boundary, boundProp)
    val decreaseButton = DecreaseSizeButton(dec.jfxId, dec.graphic, dec.step, dec.boundary, boundProp)
    val hbox = new HBox(slider, decreaseButton, increaseButton)
    HBox.setHgrow(slider, Priority.ALWAYS)
    hbox.setAlignment(Pos.CENTER)
    hbox.setPrefWidth(java.lang.Double.MAX_VALUE)
    val bBp = new BorderPane(listView, hbox, null, null, null)
    slider.valueProperty().bindBidirectional(boundProp)
    VBox.setVgrow(bBp, Priority.ALWAYS)
    bBp.setMaxHeight(java.lang.Double.MAX_VALUE)
    bBp


class LogFilePane(mutLogFileSettings: MutLogFileSettings
                  , val entries: ObservableList[LogEntry]) extends BorderPane:

  /** if a search term is added or removed this will be saved to disc */
  private val searchTermChangeListener: ListChangeListener[MutableSearchTerm] =
    def handleSearchTermChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit =
      while change.next() do
        Future(LogoRRRGlobals.persist(LogoRRRGlobals.getSettings))

    JfxUtils.mkListChangeListener[MutableSearchTerm](handleSearchTermChange)

  /** if autoscroll checkbox is enabled, monitor given log file for changes. if there are any, this will be reflected in the ui */
  val autoScrollSubscription =
    mutLogFileSettings.autoScrollActiveProperty.subscribe((old: java.lang.Boolean, newVal: java.lang.Boolean) => enableAutoscroll(newVal))

  private lazy val logTailer = LogTailer(mutLogFileSettings.getFileId, entries)

  // make sure we have a white background for our tabs - see https://github.com/rladstaetter/LogoRRR/issues/188
  setStyle("-fx-background-color: white;")

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredEntries = new FilteredList[LogEntry](entries)

  // display text to the right
  private val logTextView = new LogTextView(mutLogFileSettings, filteredEntries)

  // graphical display to the left
  private val chunkListView = LogoRRRChunkListView(filteredEntries, mutLogFileSettings, logTextView.scrollToItem, widthProperty)

  val opsToolBar = OpsToolBar(mutLogFileSettings, chunkListView, entries, filteredEntries)

  private val searchTermToolBar = new SearchTermToolBar(mutLogFileSettings, filteredEntries)

  private val textPane = LogFilePane.mkPane(
    logTextView
    , new TextSizeSlider(mutLogFileSettings.getFileId)
    , PaneDefinition(IncreaseTextSizeButton.uiNode(mutLogFileSettings.getFileId).value, TextSizeButton.mkLabel(12), TextConstants.fontSizeStep, TextConstants.MaxFontSize)
    , PaneDefinition(DecreaseTextSizeButton.uiNode(mutLogFileSettings.getFileId).value, TextSizeButton.mkLabel(8), TextConstants.fontSizeStep, TextConstants.MinFontSize)
    , mutLogFileSettings.fontSizeProperty
  )

  private val chunkPane = LogFilePane.mkPane(
    chunkListView
    , new BlockSizeSlider(mutLogFileSettings.getFileId)
    , PaneDefinition(IncreaseBlockSizeButton.uiNode(mutLogFileSettings.getFileId).value, RectButton.mkR(IncreaseBlockSizeButton.Size, IncreaseBlockSizeButton.Size, Color.GRAY), BlockConstants.BlockSizeStep, BlockConstants.MaxBlockSize)
    , PaneDefinition(DecreaseBlockSizeButton.uiNode(mutLogFileSettings.getFileId).value, RectButton.mkR(DecreaseBlockSizeButton.Size, DecreaseBlockSizeButton.Size, Color.GRAY), BlockConstants.BlockSizeStep, BlockConstants.MinBlockSize)
    , mutLogFileSettings.blockSizeProperty)


  // if active scroll automatically to the end of the list
  private lazy val autoScrollEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  def activeFilters: Seq[SearchTerm] = searchTermToolBar.activeSearchTerms()

  def enableAutoscroll(enabled: Boolean): Unit =
    if enabled then
      filteredEntries.addListener(autoScrollEventListener)
      logTailer.start()
    else
      filteredEntries.removeListener(autoScrollEventListener)
      logTailer.stop()


  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, searchTermToolBar)

  private val pane = new SplitPane(chunkPane, textPane)

  def init(): Unit =
    divider.setPosition(mutLogFileSettings.getDividerPosition)
    setTop(opsRegion)
    setCenter(pane)
    logTextView.init()
    chunkListView.init()
    enableAutoscroll(mutLogFileSettings.isAutoScrollActive)
    mutLogFileSettings.mutSearchTerms.addListener(searchTermChangeListener)


  private def divider: SplitPane.Divider = pane.getDividers.get(0)

  def getDividerPosition: Double = divider.getPosition

  /**
   * Called if a tab is selected
   */
  def recalculateChunkListView(): Unit = chunkListView.recalculateAndUpdateItems()

  def scrollToActiveElement(): Unit =
    chunkListView.scrollToActiveChunk()
    logTextView.scrollToActiveLogEntry()

  def shutdown(): Unit =
    logTextView.removeListeners()
    chunkListView.removeListeners()
    autoScrollSubscription.unsubscribe()
    mutLogFileSettings.mutSearchTerms.removeListener(searchTermChangeListener)




