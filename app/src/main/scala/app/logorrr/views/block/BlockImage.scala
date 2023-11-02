package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil}
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

object BlockImage {

  val MaxWidth = 4096

  // val MaxHeight = 4096
  val MaxHeight = 4096

  /** defines how many table cells should be rendered per list height */
  val DefaultBlocksPerPage = 4

  @deprecated val Height = 100 // remove when blockviewpane is gone


}


class BlockImage(blockNumber: Int
                 , widthProperty: ReadOnlyDoubleProperty
                 , blockSizeProperty: SimpleIntegerProperty
                 , entries: java.util.List[LogEntry]
                 , filtersProperty: ObservableList[Filter]
                 , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                 , heightProperty: SimpleIntegerProperty
                 , selectedLineNumberProperty: SimpleIntegerProperty)
  extends WritableImage(LPixelBuffer(blockNumber
    , Range(entries.get(0).lineNumber, entries.get(entries.size - 1).lineNumber)
    , RectangularShape(widthProperty.get().toInt, heightProperty.get())
    , blockSizeProperty
    , entries
    , filtersProperty
    , selectedEntryProperty
    , Array.fill(widthProperty.get().toInt * heightProperty.get())(ColorUtil.toARGB(Color.GREEN))
    , selectedLineNumberProperty)) with CanLog {

  def draw(i: Int, color: Color): Unit = {
    logError("implement me")
  }

  def shutdown(): Unit = {
    logError("implement me")
  }

}
