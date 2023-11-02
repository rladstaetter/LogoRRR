package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.control.ListCell


/**
 * A listcell which can contain one or more log entries
 *
 * @param selectedLineNumberProperty
 * @param widthProperty
 * @param blockSizeProperty
 * @param filtersProperty
 */
class ChunkListCell(selectedLineNumberProperty: SimpleIntegerProperty
                    , widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty
                    , filtersProperty: ObservableList[Filter]
                   ) extends ListCell[Chunk] with CanLog  {

  setStyle(
    """
      |-fx-padding: 0;
      |-fx-margin: 0;
      |-fx-background-insets: 0;
      |
      |""".stripMargin)

  override def updateItem(t: Chunk, empty: Boolean): Unit = JfxUtils.execOnUiThread {
    super.updateItem(t, empty)

    if (empty || Option(t).isEmpty) {
      setGraphic(null)
    } else {
      if (widthProperty.get() > 0) {
        if (blockSizeProperty.get() > 0) {
          val bv = new BlockView(t.number
            , selectedLineNumberProperty
            , filtersProperty
            , blockSizeProperty
            , widthProperty
            , selectedEntryProperty = new SimpleObjectProperty[LogEntry]()
            , entries = t.entries
            , heightProperty = new SimpleIntegerProperty(t.height))
          setGraphic(bv)
        } else {
          logTrace(s"Blocksize was ${blockSizeProperty.get()}, setting graphic to null")
          setGraphic(null)
        }
      } else {
        logTrace(s"Width was ${widthProperty.get()}, setting graphic to null")
        setGraphic(null)
      }
    }
  }
}
