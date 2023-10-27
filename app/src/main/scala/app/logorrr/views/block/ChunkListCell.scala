package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.MathUtil
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.scene.control.ListCell

object ChunkListCell {

  /**
   * Given the current blocksize, returns an appropriate height for the cell. However, it won't surpass [[BlockImage.MaxHeight]].
   *
   * @param blockSize size of blocks to display
   * @return an appropriate height for the ChunkListCell
   */
  // defines the default height for a ChunkListCell
  def calcHeight(blockSize: Int): Int = Math.min(blockSize * 10, BlockImage.MaxHeight)

}

class ChunkListCell(selectedLineNumberProperty: SimpleIntegerProperty
                    , widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty) extends ListCell[Chunk] {

  setStyle(
    """
      |-fx-padding: 0;
      |-fx-margin: 0;
      |-fx-background-insets: 0;
      |
      |""".stripMargin)

  override def updateItem(t: Chunk, empty: Boolean): Unit = {
    super.updateItem(t, empty)

    if (empty || Option(t).isEmpty) {
      setGraphic(null)
      setText(null)
    } else {

      val name = t.name
      val height = MathUtil.calcBoundedHeight(widthProperty, blockSizeProperty, t.entries)
      val bv = new BlockView(name = name
        , selectedLineNumberProperty
        , filtersProperty = new SimpleListProperty[Filter]
        , blockSizeProperty = blockSizeProperty
        , widthProperty = widthProperty
        , selectedEntryProperty = new SimpleObjectProperty[LogEntry]()
        , entries = t.entries
        , heightProperty = new SimpleIntegerProperty(height))
      setGraphic(bv)
      setText(null)
    }
  }
}
