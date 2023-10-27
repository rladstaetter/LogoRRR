package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty}
import javafx.collections.FXCollections
import javafx.scene.control.ListView


/**
 * Each ListCell contains one or more LogEntries; the cells group LogEntries together. This approach
 * allows us to use a vanilla ListView which takes care of painting the cells if needed, we don't need
 * to care about the virtual flow which is happening in the standard javafx library.
 *
 * @param entriesProperty   log entries to display
 * @param blockSizeProperty size of blocks to display
 */
class ChunkListView(val entriesProperty: SimpleListProperty[LogEntry]
                    , val blockSizeProperty: SimpleIntegerProperty)
  extends ListView[Chunk] with CanLog {

  init()

  def init(): Unit = {
    /*
    setStyle(
      """
        |-fx-padding: 0;
        |-fx-margin: 0;
        |-fx-selection-bar: transparent;
        |-fx-background-color: transparent;
        |
        |""".stripMargin)
*/
    getStylesheets.add(getClass.getResource("ChunkListView.css").toExternalForm)

    setCellFactory((lv: ListView[Chunk]) => new ChunkListCell(lv.widthProperty(), blockSizeProperty))

  }

  def cols = widthProperty().get() / blockSizeProperty.get()

  def rows = entriesProperty.size() / cols

  def height = rows * blockSizeProperty.get()

  def recalculateListViewElements(): Unit = {
    val chunks = Chunk.mkChunks(entriesProperty
      , widthProperty().get().toInt
      , boundedHeight
      , blockSizeProperty.get())
    // for (c <- chunks) {
    //  println(s"SuperChunk: ${c.name} ${c.entries.size}")
    //}
    setItems(FXCollections.observableArrayList(chunks: _*))
  }

  /** if height exceeds 10 x blocksize, return this value, else return computed height */
  private def boundedHeight: Double = {
    if (height >= blockSizeProperty.get() * 10) blockSizeProperty.get() * 10 else height
  }
}
