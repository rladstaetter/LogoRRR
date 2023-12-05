package app.logorrr.views.block

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.ListView

import scala.util.{Failure, Success, Try}


object ChunkListView {

  def apply(entries: ObservableList[LogEntry]
            , settings: MutLogFileSettings
            , selectInTextView: LogEntry => Unit
           ): ChunkListView = {
    new ChunkListView(entries
      , settings.selectedLineNumberProperty
      , settings.blockSizeProperty
      , settings.filtersProperty
      , settings.dividerPositionProperty
      , selectInTextView)
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
                    , val filtersProperty: ObservableList[Filter]
                    , val dividersProperty: SimpleDoubleProperty
                    , selectInTextView: LogEntry => Unit)
  extends ListView[Chunk]
    with CanLog {

  /**
   * If the observable list changes in any way, recalculate the items in the listview.
   */
  private val logEntriesInvalidationListener = JfxUtils.mkInvalidationListener(_ => recalculateAndUpdateItems("invalidation"))

  /** if user selects a new active log entry, recalculate and implicitly repaint */
  val selectedRp = mkRecalculateAndUpdateItemListener("selected")

  /** if blocksize changes, recalculate */
  val blockSizeRp = mkRecalculateAndUpdateItemListener("blockSize")

  /** if width changes, recalculate */
  val widthRp = mkRecalculateAndUpdateItemListener("width")

  /** if height changes, recalculate */
  val heightRp = mkRecalculateAndUpdateItemListener("height")

  def mkRecalculateAndUpdateItemListener(ctx: String): ChangeListener[Number] = new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      if (oldValue != newValue && newValue.doubleValue() != 0.0) {
        recalculateAndUpdateItems(ctx)
      }
    }
  }


  def init(): Unit = {
    getStylesheets.add(getClass.getResource("/app/logorrr/ChunkListView.css").toExternalForm)

    setCellFactory((lv: ListView[Chunk]) =>
      new ChunkListCell(selectedLineNumberProperty
        , lv.widthProperty()
        , blockSizeProperty
        , filtersProperty
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
      logWarn("ListView[Chunk] is empty, not scrolling to active chunk.")
    }
  }

  private def addListeners(): Unit = {
    logEntries.addListener(logEntriesInvalidationListener)
    selectedLineNumberProperty.addListener(selectedRp)
    blockSizeProperty.addListener(blockSizeRp)
    widthProperty().addListener(widthRp)
    heightProperty().addListener(heightRp)
  }

  def removeListeners(): Unit = {
    logEntries.removeListener(logEntriesInvalidationListener)

    selectedLineNumberProperty.removeListener(selectedRp)
    blockSizeProperty.removeListener(blockSizeRp)
    widthProperty().removeListener(widthRp)
    heightProperty().removeListener(heightRp)
  }

  /**
   * Recalculates elements of listview depending on width, height and blocksize.
   */
  def recalculateAndUpdateItems(ctx: String): Unit = { // TODO remove context information
    if (widthProperty().get() > 0 && heightProperty.get() > 0 && blockSizeProperty.get() > 0) {
      logTrace(s"!!! recalculating ($ctx)> (width: ${widthProperty().get()}, blockSize: ${blockSizeProperty.get()}, height: ${heightProperty().get()})")
      Try {
        val chunks = Chunk.mkChunks(logEntries, widthProperty, blockSizeProperty, heightProperty)
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


