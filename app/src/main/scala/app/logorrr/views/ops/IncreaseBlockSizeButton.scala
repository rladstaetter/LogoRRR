package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.block.HasBlockSizeProperty
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

object IncreaseBlockSizeButton extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode("increaseblocksizebutton-" + HashUtil.md5Sum(id))
}

class IncreaseBlockSizeButton(id: FileId, val blockSizeProperty: SimpleIntegerProperty) extends
  RectButton(width = 16
    , height = 16
    , color = Color.GRAY
    , tooltipMessage = "increase block size") with HasBlockSizeProperty {

  setId(IncreaseBlockSizeButton.uiNode(id).value)
  setOnAction(_ => {
    if (getBlockSize() + OpsToolBar.blockSizeStep < 70 * OpsToolBar.blockSizeStep) {
      setBlockSize(getBlockSize() + OpsToolBar.blockSizeStep)
    }
  })

}
