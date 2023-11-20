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
 * @param entries                    log entries to display
 * @param selectedLineNumberProperty which line is selected by the user
 * @param blockSizeProperty          size of blocks to display
 * @param filtersProperty            which filters are active
 * @param dividersProperty           position of divider of splitpane
 */
class ChunkListView(val entries: ObservableList[LogEntry]
                    , val selectedLineNumberProperty: SimpleIntegerProperty
                    , val blockSizeProperty: SimpleIntegerProperty
                    , val filtersProperty: ObservableList[Filter]
                    , val dividersProperty: SimpleDoubleProperty
                    , selectInTextView: LogEntry => Unit)
  extends ListView[Chunk] with CanLog {

  var repaints = 0

  val repaintInvalidationListener = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = repaint()
  }

  val repaintChangeListener = JfxUtils.onNew[Number](_ => repaint())

  def addListeners(): Unit = {
    entries.addListener(repaintInvalidationListener)

    selectedLineNumberProperty.addListener(repaintChangeListener)
    dividersProperty.addListener(repaintChangeListener)
    blockSizeProperty.addListener(repaintChangeListener)
    widthProperty().addListener(repaintChangeListener)
    heightProperty().addListener(repaintChangeListener)
  }

  def removeListeners(): Unit = {
    entries.removeListener(repaintInvalidationListener)

    selectedLineNumberProperty.addListener(repaintChangeListener)
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


  // if width/height of display is changed, also elements of this listview will change
  // and shuffle between cells. this method recreates all listview entries.
  def updateItems(): Unit = Try {
    val chunks =
      if (widthProperty.get() > 0) {
        if (blockSizeProperty.get() > 0) {
          if (heightProperty().get() > 0) {
            Chunk.mkChunks(entries, widthProperty, blockSizeProperty, heightProperty)
          } else {
            logWarn("heightProperty was " + heightProperty().get())
            Seq()
          }
        } else {
          logWarn("blockSizeProperty was " + blockSizeProperty.get())
          Seq()
        }
      } else {
        logWarn("widthProperty was " + widthProperty().get())
        Seq()
      }
    val chunks1 = FXCollections.observableArrayList(chunks: _*)
    setItems(chunks1)
  } match {
    case Success(_) => ()
    case Failure(exception) => logException(exception.getMessage, exception)
  }

  def doRepaint: Boolean = widthProperty().get() > 0 && heightProperty.get() > 0 && blockSizeProperty.get() > 0

  /**
   * Repaint method takes care of painting the blocks on the raw byte array.
   *
   * This method may be called more often than necessary, the 'doRepaint' if statement makes sure we don't paint
   * if not all prerequisites are met. Also, this has to run on the javafx thread such that those updates are displayed
   * correctly.
   */
  // do not remove JfxUtils.execOnUiThread
  def repaint(): Unit = JfxUtils.execOnUiThread {
    if (doRepaint) {
      repaints += 1
      timeR({
        updateItems()
        refresh()
      }, s"Repainted #$repaints")
    } else {
      val msg = s"Not repainted: width=${widthProperty().get()}, blockSize=${blockSizeProperty.get()}, height: ${heightProperty().get()}"
      logTrace(msg)
    }
  }
}


