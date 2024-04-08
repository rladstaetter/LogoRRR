package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.views.block.{BlockConstants, HasBlockSizeProperty}
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

object DecreaseBlockSizeButton extends UiNodeAware {
  /** size of icon to decrease block size */
  val Size = 8

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseBlockSizeButton])
}

class DecreaseBlockSizeButton(id: FileId, val blockSizeProperty: SimpleIntegerProperty) extends
  SquareButton(size = DecreaseBlockSizeButton.Size
    , color = Color.GRAY
    , tooltipMessage = "decrease block size") with HasBlockSizeProperty {

  setId(DecreaseBlockSizeButton.uiNode(id).value)
  setOnAction(_ => {
    if (getBlockSize - OpsToolBar.blockSizeStep > BlockConstants.MinSize) {
      setBlockSize(getBlockSize - OpsToolBar.blockSizeStep)
    } else {
      setBlockSize(BlockConstants.MinSize)
    }
  })

}
