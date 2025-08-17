package app.logorrr.views.logfiletab

import app.logorrr.io.FileId
import app.logorrr.views.block.BlockConstants
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.Slider

object BlockSizeSlider extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[BlockSizeSlider])
}

class BlockSizeSlider(fileId: FileId) extends Slider {
  setId(BlockSizeSlider.uiNode(fileId).value)
  setMin(BlockConstants.MinBlockSize)
  setMax(BlockConstants.MaxBlockSize)
  setMajorTickUnit(BlockConstants.BlockSizeStep)
  setMinorTickCount(0)
  setSnapToTicks(true)
  setShowTickLabels(false)
  setShowTickMarks(false)
}



