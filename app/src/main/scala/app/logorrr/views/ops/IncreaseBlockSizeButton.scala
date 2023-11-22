package app.logorrr.views.ops

import app.logorrr.views.block.HasBlockSizeProperty
import app.logorrr.views.search.OpsToolBar
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

class IncreaseBlockSizeButton(val blockSizeProperty: SimpleIntegerProperty) extends
  RectButton(width = 16
    , height = 16
    , color = Color.GRAY
    , tooltipMessage = "increase block size") with HasBlockSizeProperty {

  setOnAction(_ => {
    if (getBlockSize() + OpsToolBar.blockSizeStep < 70 * OpsToolBar.blockSizeStep) {
      setBlockSize(getBlockSize() + OpsToolBar.blockSizeStep)
    }
  })

}
