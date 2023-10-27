package app.logorrr.util

import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListCell
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}

import scala.math.BigDecimal.RoundingMode

object MathUtil {
  def roundUp(nrRows: Double): Int = {
    val decimal = BigDecimal.double2bigDecimal(nrRows)
    val decimal1 = decimal.setScale(0, RoundingMode.UP)
    decimal1.intValue
  }

  def colRowsHeight(widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty
                    , nrEntries: Int): (Int, Int, Int) = {

    val cols: Int = MathUtil.roundUp(widthProperty.get() / blockSizeProperty.get())
    val rows: Int = if (nrEntries < cols) 1 else nrEntries / cols
    val height: Int = rows * blockSizeProperty.get()

    (cols, rows, height)
  }

  def calcBoundedHeight(widthProperty: ReadOnlyDoubleProperty
                        , blockSizeProperty: SimpleIntegerProperty
                        , entriesProperty: java.util.List[LogEntry]): Int = {

    Math.min(MathUtil.colRowsHeight(widthProperty, blockSizeProperty, entriesProperty.size())._3, ChunkListCell.calcHeight(blockSizeProperty.get()))
  }
}
