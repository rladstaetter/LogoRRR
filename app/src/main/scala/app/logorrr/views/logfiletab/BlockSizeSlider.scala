package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.block.BlockConstants
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Slider

object BlockSizeSlider extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[BlockSizeSlider])

class BlockSizeSlider extends Slider with BoundFileId(BlockSizeSlider.uiNode(_).value):
  setMin(BlockConstants.MinBlockSize)
  setMax(BlockConstants.MaxBlockSize)
  setMajorTickUnit(BlockConstants.BlockSizeStep)
  setMinorTickCount(0)
  setSnapToTicks(true)
  setShowTickLabels(false)
  setShowTickMarks(false)

