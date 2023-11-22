package app.logorrr.util

import app.logorrr.model.LogEntry
import app.logorrr.views.block.BlockImage
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}

import scala.math.BigDecimal.RoundingMode

object MathUtil extends CanLog {

  def roundUp(nrRows: Double): Int = {
    BigDecimal.double2bigDecimal(nrRows).setScale(0, RoundingMode.UP).intValue
  }

  def roundDown(nrRows: Double): Int = {
    BigDecimal.double2bigDecimal(nrRows).setScale(0, RoundingMode.DOWN).intValue
  }

  def calcBoundedHeight(widthProperty: ReadOnlyDoubleProperty
                        , blockSizeProperty: SimpleIntegerProperty
                        , entriesProperty: java.util.List[LogEntry]
                        , listViewHeightProperty: ReadOnlyDoubleProperty): (Int, Int, Int) = {
    val w = if (widthProperty.get() - BlockImage.ScrollBarWidth >= 0) widthProperty.get() - BlockImage.ScrollBarWidth else widthProperty.get()

    val cols: Int = MathUtil.roundUp(w / blockSizeProperty.get())
    val rows: Int = if (entriesProperty.size() < cols) 1 else entriesProperty.size() / cols

    // per default, use 4 cells per visible page, align height with blocksize such that
    // we don't get artifacts. Further, make sure that the calculated height does not exceed
    // MaxHeight of underlying texture painting mechanism.

    // DO NOT REMOVE since the first division throws away the remainder and the multiplication
    // yields the best approximation of MaxHeight.
    val maxHeight = (BlockImage.MaxHeight / blockSizeProperty.get()) * blockSizeProperty.get()
    val height = Math.min(MathUtil.roundDown((listViewHeightProperty.get() / BlockImage.DefaultBlocksPerPage) / blockSizeProperty.get()) * blockSizeProperty.get(), maxHeight)
    (cols, rows, height)
  }
}
