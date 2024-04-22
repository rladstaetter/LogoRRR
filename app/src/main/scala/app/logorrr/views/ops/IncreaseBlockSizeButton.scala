package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.views.block.HasBlockSizeProperty
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

object IncreaseBlockSizeButton extends UiNodeFileIdAware {

  /** size of icon to increase block size */
  val Size = 16

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[IncreaseBlockSizeButton])
}

class IncreaseBlockSizeButton(id: FileId, val blockSizeProperty: SimpleIntegerProperty)
  extends SquareButton(size = IncreaseBlockSizeButton.Size
    , color = Color.GRAY
    , tooltipMessage = "increase block size") with HasBlockSizeProperty {

  setId(IncreaseBlockSizeButton.uiNode(id).value)
  setOnAction(_ => {

    if (getBlockSize + OpsToolBar.blockSizeStep < OpsToolBar.MaxBlockSize) {
      setBlockSize(getBlockSize + OpsToolBar.blockSizeStep)
    }
  })

}
