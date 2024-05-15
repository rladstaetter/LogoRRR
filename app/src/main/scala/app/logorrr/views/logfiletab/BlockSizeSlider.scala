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
  getStylesheets.add(getClass.getResource("/app/logorrr/Slider.css").toExternalForm)
  setMin(BlockConstants.MinBlockSize)
  setMax(BlockConstants.MaxBlockSize)
  setShowTickLabels(false)
  setShowTickMarks(false)
}
