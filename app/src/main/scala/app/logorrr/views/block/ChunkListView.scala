package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.search.Filter
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.ListView


/**
 * Each ListCell contains one or more LogEntries; the cells group LogEntries together. This approach
 * allows us to use a vanilla ListView which takes care of painting the cells if needed, we don't need
 * to care about the virtual flow which is happening in the standard javafx library.
 *
 * @param entriesProperty   log entries to display
 * @param blockSizeProperty size of blocks to display
 */
class ChunkListView(val entriesProperty: ObservableList[LogEntry]
                    , val selectedLineNumberProperty: SimpleIntegerProperty
                    , val blockSizeProperty: SimpleIntegerProperty
                    , val filtersProperty: ObservableList[Filter])
  extends ListView[Chunk] with CanLog {

  getStylesheets.add(getClass.getResource("/app/logorrr/views/block/ChunkListView.css").toExternalForm)

  setCellFactory((lv: ListView[Chunk]) => new ChunkListCell(
    selectedLineNumberProperty
    , lv.widthProperty()
    , blockSizeProperty
    , filtersProperty))

  // if width/height of display is changed, also elements of this listview will change
  // and shuffle between cells. this method recreates all listview entries.
  def updateItems(): Unit = {
    val chunks =
      if (widthProperty.get() > 0) {
        if (blockSizeProperty.get() > 0) {
          Chunk.mkChunks(entriesProperty, widthProperty, blockSizeProperty, heightProperty)
        } else {
          Seq()
        }
      } else {
        Seq()
      }
    setItems(FXCollections.observableArrayList(chunks: _*))
  }


}
