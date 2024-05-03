package app.logorrr.views.block

import javafx.beans.property.SimpleIntegerProperty

trait HasBlockSizeProperty {

  /** size of a rectangle which represents a log line */
  val blockSizeProperty: SimpleIntegerProperty

  def setBlockSize(blockSize: Int): Unit = blockSizeProperty.set(blockSize)

  def getBlockSize: Int = blockSizeProperty.get()

}
