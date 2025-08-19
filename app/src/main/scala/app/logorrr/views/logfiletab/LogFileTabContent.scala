package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.SearchTerm
import app.logorrr.views.block.BlockConstants
import app.logorrr.views.ops._
import app.logorrr.views.search.{OpsToolBar, SearchTermToolBar}
import app.logorrr.views.text.LogTextView
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.beans.property.Property
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.{ListView, Slider, SplitPane}
import javafx.scene.layout.{BorderPane, HBox, Priority, VBox}
import javafx.scene.paint.Color

case class PaneDefinition(jfxId: String, graphic: Node, step: Int, boundary: Int)

object LogFileTabContent {

  /** wire pane and slider together */
  def mkPane(listView: ListView[_]
             , slider: Slider
             , inc: PaneDefinition
             , dec: PaneDefinition
             , boundProp: Property[Number]): BorderPane = {
    val increaseButton = IncreaseSizeButton(inc.jfxId, inc.graphic, inc.step, inc.boundary, boundProp)
    val decreaseButton = DecreaseSizeButton(dec.jfxId, dec.graphic, dec.step, dec.boundary, boundProp)
    val hbox = new HBox(slider, decreaseButton, increaseButton)
    HBox.setHgrow(slider, Priority.ALWAYS)
    hbox.setAlignment(Pos.CENTER)
    //BorderPane.HBox.setHgrow(hbox, Priority.ALWAYS)
    hbox.setPrefWidth(java.lang.Double.MAX_VALUE)
    val bBp = new BorderPane(listView, hbox, null, null, null)
    slider.valueProperty().bindBidirectional(boundProp)
    VBox.setVgrow(bBp, Priority.ALWAYS)
    bBp.setMaxHeight(java.lang.Double.MAX_VALUE)
    bBp
  }

}

class LogFileTabContent(mutLogFileSettings: MutLogFileSettings
                        , val entries: ObservableList[LogEntry]) extends BorderPane {

  // make sure we have a white background for our tabs - see https://github.com/rladstaetter/LogoRRR/issues/188
  setStyle("-fx-background-color: white;")

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](entries)

  // display text to the right
  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // graphical display to the left
  private val chunkListView = LogoRRRChunkListView(filteredList, mutLogFileSettings, logTextView.scrollToItem, widthProperty)

  private val textPane = LogFileTabContent.mkPane(
    logTextView
    , new TextSizeSlider(mutLogFileSettings.getFileId)
    , PaneDefinition(IncreaseTextSizeButton.uiNode(mutLogFileSettings.getFileId).value, TextSizeButton.mkLabel(12), TextConstants.fontSizeStep, TextConstants.MaxFontSize)
    , PaneDefinition(DecreaseTextSizeButton.uiNode(mutLogFileSettings.getFileId).value, TextSizeButton.mkLabel(8), TextConstants.fontSizeStep, TextConstants.MinFontSize)
    , mutLogFileSettings.fontSizeProperty
  )

  private val chunkPane = LogFileTabContent.mkPane(
    chunkListView
    , new BlockSizeSlider(mutLogFileSettings.getFileId)
    , PaneDefinition(IncreaseBlockSizeButton.uiNode(mutLogFileSettings.getFileId).value, RectButton.mkR(IncreaseBlockSizeButton.Size, IncreaseBlockSizeButton.Size, Color.GRAY), BlockConstants.BlockSizeStep, BlockConstants.MaxBlockSize)
    , PaneDefinition(DecreaseBlockSizeButton.uiNode(mutLogFileSettings.getFileId).value, RectButton.mkR(DecreaseBlockSizeButton.Size, DecreaseBlockSizeButton.Size, Color.GRAY), BlockConstants.BlockSizeStep, BlockConstants.MinBlockSize)
    , mutLogFileSettings.blockSizeProperty)


  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  def activeFilters: Seq[SearchTerm] = searchTermToolBar.activeFilters()

  def addTailerListener(): Unit = filteredList.addListener(scrollToEndEventListener)

  def removeTailerListener(): Unit = filteredList.removeListener(scrollToEndEventListener)


  val opsToolBar = new OpsToolBar(mutLogFileSettings.getFileId
    , mutLogFileSettings
    , chunkListView
    , mutLogFileSettings.filtersProperty.add(_)
    , entries
    , filteredList
    , mutLogFileSettings.blockSizeProperty)

  private val searchTermToolBar = {
    val fbtb =
      new SearchTermToolBar(
        mutLogFileSettings
        , filteredList
        , mutLogFileSettings.filtersProperty.remove(_)
      )
    fbtb.searchTermsProperty.bind(mutLogFileSettings.filtersProperty)
    fbtb
  }
  /*
  val timeOpsToolBar = new TimeOpsToolBar(mutLogFileSettings
    , chunkListView
    , entries
    , filteredList)
*/

  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, searchTermToolBar)

  private val pane = new SplitPane(chunkPane, textPane)

  def init(): Unit = {
    divider.setPosition(mutLogFileSettings.getDividerPosition)

    setTop(opsRegion)
    setCenter(pane)

    logTextView.init()
    chunkListView.init()

  }


  private def divider: SplitPane.Divider = pane.getDividers.get(0)

  def getDividerPosition: Double = divider.getPosition

  def removeListeners(): Unit = {
    logTextView.removeListeners()
    chunkListView.removeListeners()
  }

  /**
   * Called if a tab is selected
   */
  def recalculateChunkListView(): Unit = chunkListView.recalculateAndUpdateItems()

  def scrollToActiveElement(): Unit = {
    chunkListView.scrollToActiveChunk()
    logTextView.scrollToActiveLogEntry()
  }

}




