package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.block.BlockConstants
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Slider

object BlockSizeSlider extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[BlockSizeSlider])

class BlockSizeSlider extends Slider:
  setMin(BlockConstants.MinBlockSize)
  setMax(BlockConstants.MaxBlockSize)
  setMajorTickUnit(BlockConstants.BlockSizeStep)
  setMinorTickCount(0)
  setSnapToTicks(true)
  setShowTickLabels(false)
  setShowTickMarks(false)

  def bind(fileProperty: SimpleObjectProperty[FileId]): Unit =
    idProperty.bind(Bindings.createObjectBinding(() => BlockSizeSlider.uiNode(fileProperty.get()).value, fileProperty))

  def unbind(): Unit = idProperty.unbind()
