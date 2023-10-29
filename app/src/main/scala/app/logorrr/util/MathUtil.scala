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
    val cols: Int = MathUtil.roundUp(widthProperty.get() / blockSizeProperty.get())
    val rows: Int = if (entriesProperty.size() < cols) 1 else entriesProperty.size() / cols
    // per default, use 4 cells per visible page, align height with blocksize such that
    // we don't get artifacts. Further, make sure that the calculated height does not exceed
    // MaxHeight of underlying texture painting mechanism.
    val height = Math.min(MathUtil.roundDown((listViewHeightProperty.get() / 4) / blockSizeProperty.get()) * blockSizeProperty.get(), (BlockImage.MaxHeight / blockSizeProperty.get()) * blockSizeProperty.get())
    // val height: Int = Math.min(rows * blockSizeProperty.get(), BlockImage.MaxHeight)
    (cols, rows, height)
  }
}