package app.logorrr.views.block

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.ListView

import scala.util.{Failure, Success, Try}


object ChunkListView {

  def apply(entries: ObservableList[LogEntry]
            , settings: MutLogFileSettings): ChunkListView = {
    new ChunkListView(entries
      , settings.selectedLineNumberProperty
      , settings.blockSizeProperty
      , settings.filtersProperty
      , settings.dividerPositionProperty)
  }
}

/**
 * Each ListCell contains one or more LogEntries; the cells group LogEntries together. This approach
 * allows us to use a vanilla ListView which takes care of painting the cells if needed, we don't need
 * to care about the virtual flow which is happening in the standard javafx library.
 *
 * @param entries           log entries to display
 * @param blockSizeProperty size of blocks to display
 */
class ChunkListView(val entries: ObservableList[LogEntry]
                    , val selectedLineNumberProperty: SimpleIntegerProperty
                    , val blockSizeProperty: SimpleIntegerProperty
                    , val filtersProperty: ObservableList[Filter]
                    , val dividersProperty: SimpleDoubleProperty)
  extends ListView[Chunk] with CanLog {

  var repaints = 0

  val repaintInvalidationListener = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = repaint()
  }

  getStylesheets.add(getClass.getResource("/app/logorrr/views/block/ChunkListView.css").toExternalForm)

  setCellFactory((lv: ListView[Chunk]) => new ChunkListCell(
    selectedLineNumberProperty
    , lv.widthProperty()
    , blockSizeProperty
    , filtersProperty))


  def addListener(): Unit = {
    entries.addListener(repaintInvalidationListener)
    blockSizeProperty.addListener(repaintInvalidationListener)
    dividersProperty.addListener(repaintInvalidationListener)
  }

  def removeListener(): Unit = {
    entries.removeListener(repaintInvalidationListener)
    blockSizeProperty.removeListener(repaintInvalidationListener)
    dividersProperty.removeListener(repaintInvalidationListener)
  }

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
    case Success(value) => ()
    case Failure(exception) => logException(exception.getMessage, exception)
  }

  def doRepaint: Boolean = widthProperty().get() > 0 & blockSizeProperty.get() > 0 && heightProperty.get() > 0

  def repaint(): Unit = {
    if (doRepaint) {
      repaints += 1
      timeR({
        updateItems()
        refresh()
      }, s"Repaint #$repaints")
    } else {
      logTrace(s"Not repainting: width=${widthProperty().get()}, blockSize=${blockSizeProperty.get()}, height: ${heightProperty().get()}")
    }
  }

}
