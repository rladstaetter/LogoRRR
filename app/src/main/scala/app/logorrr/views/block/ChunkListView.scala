package app.logorrr.views.block

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.{InvalidationListener, Observable}
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
 * Each ListCell contains one or more Logentries - those regions are called 'Chunks'.; Those chunks
 * group LogEntries; this grouping serves no other purpose than to optimize painting all entries via
 * a ListView. In this way we get a stable and prooven virtual flow implementation under the hood and we
 * don't have to reinvent this again.
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

  private val repaintInvalidationListener = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      repaint()
    }
  }

  val repaintChangeListener = JfxUtils.onNew[Number](_ => repaint())
  val refreshListener = JfxUtils.onNew[Number](_ => {
    repaint()
  })

  def init(): Unit = {
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
      logTrace("Empty entries")
    }
  }

  private def addListeners(): Unit = {
    logEntries.addListener(repaintInvalidationListener)

    selectedLineNumberProperty.addListener(refreshListener)
    dividersProperty.addListener(repaintChangeListener)
    blockSizeProperty.addListener(repaintChangeListener)
    widthProperty().addListener(repaintChangeListener)
    heightProperty().addListener(repaintChangeListener)
  }

  def removeListeners(): Unit = {
    logEntries.removeListener(repaintInvalidationListener)

    selectedLineNumberProperty.addListener(refreshListener)
    dividersProperty.removeListener(repaintChangeListener)
    blockSizeProperty.removeListener(repaintChangeListener)
    widthProperty().removeListener(repaintChangeListener)
    heightProperty().removeListener(repaintChangeListener)
  }

  getStylesheets.add(getClass.getResource("/app/logorrr/ChunkListView.css").toExternalForm)

  setCellFactory((lv: ListView[Chunk]) => new ChunkListCell(selectedLineNumberProperty
    , lv.widthProperty()
    , blockSizeProperty
    , filtersProperty
    , selectInTextView))

  /**
   * Repaint method takes care of painting the blocks on the raw byte array.
   *
   * This method may be called more often than necessary, the 'doRepaint' if statement makes sure we don't paint
   * if not all prerequisites are met. Also, this has to run on the javafx thread such that those updates are displayed
   * correctly.
   */
  // do not remove JfxUtils.execOnUiThread
  def repaint(): Unit = {
    if (widthProperty().get() > 0 && heightProperty.get() > 0 && blockSizeProperty.get() > 0) {
      Try {
        val chunks = Chunk.mkChunks(logEntries, widthProperty, blockSizeProperty, heightProperty)
        setItems(FXCollections.observableArrayList(chunks: _*))
      } match {
        case Success(_) => ()
        case Failure(exception) => logException(exception.getMessage, exception)
      }
      refresh()
    } else {
      val msg = s"Not repainted. (width: ${widthProperty().get()}, blockSize: ${blockSizeProperty.get()}, height: ${heightProperty().get()})"
      logTrace(msg)
    }
  }
}


