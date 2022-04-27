package app.logorrr.views.block

import javafx.beans.property.SimpleIntegerProperty

trait HasBlockSizeProperty {

  val blockSizeProperty: SimpleIntegerProperty

  def setBlockSize(blockSize: Int): Unit = blockSizeProperty.set(blockSize)

  def getBlockSize(): Int = blockSizeProperty.get()


}
