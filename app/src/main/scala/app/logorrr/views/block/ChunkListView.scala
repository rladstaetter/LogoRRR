package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, MathUtil}
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
                    , val selectedLineNumberProperty: SimpleIntegerProperty
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

    setCellFactory((lv: ListView[Chunk]) => new ChunkListCell(
      selectedLineNumberProperty
      , lv.widthProperty()
      , blockSizeProperty))

  }

  def cols: Int = MathUtil.roundUp(widthProperty().get() / blockSizeProperty.get())

  def rows: Int = entriesProperty.size() / cols

  def height: Int = rows * blockSizeProperty.get()


  // if width/height of display is changed, also elements of this listview will change
  // and shuffle between cells. this method recreates all listview entries.
  def recalculateListViewElements(): Unit = {
    val chunks = Chunk.mkChunks(entriesProperty
      , widthProperty().get().toInt
      , MathUtil.calcBoundedHeight(widthProperty, blockSizeProperty, entriesProperty)
      , blockSizeProperty.get())
    setItems(FXCollections.observableArrayList(chunks: _*))
  }


}
