package app.logorrr.views.visual

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.block.ChunkListView
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty}
import javafx.collections.ObservableList

class LogVisualView(selectedLineNumberProperty: SimpleIntegerProperty
                    , filtersProperty: SimpleListProperty[Filter]
                    , entries: ObservableList[LogEntry]
                    , blockSizeProperty: SimpleIntegerProperty)
  extends ChunkListView(entries
    , selectedLineNumberProperty
    , blockSizeProperty
    , filtersProperty) with CanLog {

  widthProperty().addListener(JfxUtils.onNew[Number](n => {
    logTrace("repaint width:" + n.intValue())
    updateItems()
    refresh()
  }))


  /**
   * makes sure that last line of block view is visible, useful for autoscroll feature
   */
  def scrollToEnd(): Unit = () // blockViewPane.scrollToEnd()


}
