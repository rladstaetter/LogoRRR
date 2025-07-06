package app.logorrr.jfxbfr

import app.logorrr.model.LogEntry
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import javafx.geometry.Orientation
import javafx.scene.control.skin.VirtualFlow
import javafx.scene.control.{ListView, ScrollBar, Skin, SkinBase}
import net.ladstatt.util.log.CanLog

import java.lang
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Failure, Success, Try}


object ChunkListView {

  def lookupVirtualFlow(skin: Skin[_]): Option[VirtualFlow[ChunkListCell]] = {
    Option(skin match {
      case skinBase: SkinBase[_] =>
        skinBase.getChildren.asScala.find(_.getStyleClass.contains("virtual-flow")).orNull.asInstanceOf[VirtualFlow[ChunkListCell]]
      case _ =>
        null
    })
  }

  def lookupScrollBar(flow: VirtualFlow[ChunkListCell], orientation: Orientation): Option[ScrollBar] = {
    Option(flow.getChildrenUnmodifiable.toArray.collectFirst { case sb: ScrollBar if sb.getOrientation == orientation => sb }.orNull)
  }

  def calcListViewWidth(listViewWidth: Double): Double = {
    if (listViewWidth - ChunkImage.getScrollBarWidth >= 0) listViewWidth - ChunkImage.getScrollBarWidth else listViewWidth
  }

}


/**
 * Each ListCell contains one or more Logentries - those regions are called 'Chunks'.
 *
 * Those chunks group LogEntries; this grouping serves no other purpose than to optimize painting
 * all entries via a ListView. In this way we get a stable and proven virtual flow implementation
 * under the hood and we don't have to reinvent this again.
 *
 * @param logEntries                 log entries to display
 * @param selectedLineNumberProperty which line is selected by the user
 * @param blockSizeProperty          size of blocks to display
 * @param filtersProperty            which filters are active
 * @param dividersProperty           position of divider of splitpane
 */
class ChunkListView(val logEntries: ObservableList[LogEntry]
                    , val selectedLineNumberProperty: SimpleIntegerProperty
                    , val blockSizeProperty: SimpleIntegerProperty
                    , val filtersProperty: ObservableList[_ <: Fltr[_]]
                    , val dividersProperty: SimpleDoubleProperty
                    , val firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                    , val lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                    , selectInTextView: LogEntry => Unit)
  extends ListView[Chunk]
    with CanLog {

  /**
   * What should happen if the scrollbar appears/vanishes
   */
  private val scrollBarVisibilityListener = new ChangeListener[lang.Boolean] {
    override def changed(observableValue: ObservableValue[_ <: lang.Boolean], t: lang.Boolean, isVisible: lang.Boolean): Unit = {
      if (isVisible) {
        ChunkImage.setScrollBarWidth(ChunkImage.DefaultScrollBarWidth)
        recalculateAndUpdateItems("scrollbar visible")
      } else {
        ChunkImage.setScrollBarWidth(0)
        recalculateAndUpdateItems("scrollbar invisible")
      }
    }
  }

  // needed to get access to the scrollbar
  private val chunkListViewSkinListener: ChangeListener[Skin[_]] = new ChangeListener[Skin[_]] {
    override def changed(observableValue: ObservableValue[_ <: Skin[_]], t: Skin[_], currentSkin: Skin[_]): Unit = {
      for {skin <- Option(currentSkin)
           flow <- ChunkListView.lookupVirtualFlow(skin)
           horizontalScrollbar <- ChunkListView.lookupScrollBar(flow, Orientation.HORIZONTAL)
           verticalScrollbar <- ChunkListView.lookupScrollBar(flow, Orientation.VERTICAL)} {
        horizontalScrollbar.setVisible(false)
        verticalScrollbar.visibleProperty().addListener(scrollBarVisibilityListener)
      }
    }
  }

  /**
   * If the observable list changes in any way, recalculate the items in the listview.
   */
  private val logEntriesInvalidationListener = JfxUtils.mkInvalidationListener(_ => recalculateAndUpdateItems("invalidation"))

  /** if user selects a new active log entry, recalculate and implicitly repaint */
  private val selectedRp = mkRecalculateAndUpdateItemListener("selected")

  private val firstVisibleRp = mkRecalculateAndUpdateItemListener("firstVisibleRp")

  private val lastVisibleRp = mkRecalculateAndUpdateItemListener("lastVisibleRp")

  /** if blocksize changes, recalculate */
  private val blockSizeRp = mkRecalculateAndUpdateItemListener("blockSize")

  /** if width changes, recalculate */
  private val widthRp = mkRecalculateAndUpdateItemListener("width")

  /** if height changes, recalculate */
  private val heightRp = mkRecalculateAndUpdateItemListener("height")

  //  private val changingScrollbarVisibilityRp = mkRecalculateAndUpdateItemListener("scrollbar")

  // context variable just here for debugging
  private def mkRecalculateAndUpdateItemListener(ctx: String): ChangeListener[Number] = (_: ObservableValue[_ <: Number], oldValue: Number, newValue: Number) => {
    if (oldValue != newValue && newValue.doubleValue() != 0.0) {
      recalculateAndUpdateItems(ctx)
    }
  }


  def init(): Unit = {
    getStylesheets.add(getClass.getResource("/app/logorrr/ChunkListView.css").toExternalForm)

    setCellFactory((lv: ListView[Chunk]) =>
      new ChunkListCell(selectedLineNumberProperty
        , lv.widthProperty()
        , blockSizeProperty
        , filtersProperty
        , firstVisibleTextCellIndexProperty
        , lastVisibleTextCellIndexProperty
        , selectInTextView)
    )

    addListeners()
  }

  def scrollToActiveChunk(): Unit = {
    if (!getItems.isEmpty) {
      val filteredChunks = getItems.filtered(c => {
        var found = false
        c.entries.forEach(e => if (found) found else {
          found = e.lineNumber == selectedLineNumberProperty.get()
        })
        found
      })
      if (!filteredChunks.isEmpty) {
        Option(filteredChunks.get(0)) match {
          case Some(chunk) =>
            getSelectionModel.select(chunk)
            val relativeIndex = getItems.indexOf(chunk)
            getSelectionModel.select(relativeIndex)
            JfxUtils.scrollTo[Chunk](this, chunk.height, relativeIndex)
          case None =>
        }
      }
    } else {
      logTrace("ListView[Chunk] is empty, not scrolling to active chunk.")
    }
  }

  /** invalidation listener has to be disabled when manipulating log entries (needed for setting the timestamp for example) */
  def addInvalidationListener(): Unit = logEntries.addListener(logEntriesInvalidationListener)

  def removeInvalidationListener(): Unit = logEntries.removeListener(logEntriesInvalidationListener)

  private def addListeners(): Unit = {
    addInvalidationListener()
    selectedLineNumberProperty.addListener(selectedRp)
    firstVisibleTextCellIndexProperty.addListener(firstVisibleRp)
    lastVisibleTextCellIndexProperty.addListener(lastVisibleRp)
    blockSizeProperty.addListener(blockSizeRp)
    widthProperty().addListener(widthRp)
    heightProperty().addListener(heightRp)
    skinProperty().addListener(chunkListViewSkinListener)
  }


  def removeListeners(): Unit = {
    removeInvalidationListener()
    selectedLineNumberProperty.removeListener(selectedRp)
    firstVisibleTextCellIndexProperty.removeListener(firstVisibleRp)
    lastVisibleTextCellIndexProperty.removeListener(lastVisibleRp)
    blockSizeProperty.removeListener(blockSizeRp)
    widthProperty().removeListener(widthRp)
    heightProperty().removeListener(heightRp)


    for {skin <- Option(getSkin)
         flow <- ChunkListView.lookupVirtualFlow(skin)
         verticalScrollbar <- ChunkListView.lookupScrollBar(flow, Orientation.VERTICAL)} {
      verticalScrollbar.visibleProperty().removeListener(scrollBarVisibilityListener)
    }

    skinProperty().removeListener(chunkListViewSkinListener)
  }


  /**
   * Recalculates elements of listview depending on width, height and blocksize.
   */
  def recalculateAndUpdateItems(ctx: String): Unit = { // TODO remove context information
    if (widthProperty().get() > 0 && heightProperty.get() > 0 && blockSizeProperty.get() > 0) {
      logTrace(s"recalculating ($ctx)> (width: ${widthProperty().get()}, blockSize: ${blockSizeProperty.get()}, height: ${heightProperty().get()})")
      Try {
        val width = ChunkListView.calcListViewWidth(widthProperty.get())
        val chunks = Chunk.mkChunks(logEntries, blockSizeProperty.get(), width, heightProperty.get(), Chunk.ChunksPerVisibleViewPort)
        setItems(FXCollections.observableArrayList(chunks: _*))
      } match {
        case Success(_) => ()
        case Failure(exception) => logException(exception.getMessage, exception)
      }
      refresh()
    } else {
      logTrace(s"NOT recalculating ($ctx)> (width: ${widthProperty().get()}, blockSize: ${blockSizeProperty.get()}, height: ${heightProperty().get()})")
    }
  }
}


