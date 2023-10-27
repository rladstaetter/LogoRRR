package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.scene.control.ListCell

class ChunkListCell(widthProperty: ReadOnlyDoubleProperty
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
      // println("empty or empty list")
      setGraphic(null)
      setText(null)
    } else {
      // calculate height of cell
      // we need the number of entries and the width to calculate the height
      // println(t.name)
      /*
            val cols = widthProperty.get() / blockSizeProperty.get()
            val height = t.entries.size
            val heightProperty = new SimpleIntegerProperty(10)
      */
      // TODO set blockview
      val name = t.name
      //val height = BlockView.calcVirtualHeight(blockSizeProperty.get(),blockSizeProperty.get(),widthProperty.get().toInt,t.entries.size)
      val height = blockSizeProperty.get() * 10
      // println(s"Creating $name,width ${widthProperty.get()}, height: $height, entries: " + t.entries.size)

      val bv = new BlockView(name = name
        , selectedLineNumberProperty = new SimpleIntegerProperty(0)
        , filtersProperty = new SimpleListProperty[Filter]
        , blockSizeProperty = new SimpleIntegerProperty(10)
        , outerWidthProperty = widthProperty
        , selectedElemProperty = new SimpleObjectProperty[LogEntry]()
        , entries = t.entries
        , heightProperty = new SimpleIntegerProperty(height))
      //, heightProperty)
      setGraphic(bv)
      setText(null)
    }
  }
}
