package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil}
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

object BlockImage {

  val MaxWidth = 4096

  val MaxHeight = 4096

  @deprecated val Height = 100 // remove when blockviewpane is gone

}


class BlockImage(name: String
                 , widthProperty: ReadOnlyDoubleProperty
                 , blockSizeProperty: SimpleIntegerProperty
                 , entries: java.util.List[LogEntry]
                 , filtersProperty: SimpleListProperty[Filter]
                 , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                 , heightProperty: SimpleIntegerProperty)
  extends WritableImage(LPixelBuffer(name
    , RectangularShape(widthProperty.get(), heightProperty.get())
    , blockSizeProperty
    , entries
    , filtersProperty
    , selectedEntryProperty
    , Array.fill((widthProperty.get() * heightProperty.get()).toInt)(ColorUtil.toARGB(Color.GREEN)))) with CanLog {

  def draw(i: Int, color: Color): Unit = {
    logError("implement me")
  }

  def shutdown(): Unit = {
    logError("implement me")
  }

}
