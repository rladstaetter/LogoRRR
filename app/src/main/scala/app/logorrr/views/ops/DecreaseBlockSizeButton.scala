package app.logorrr.views.ops

import app.logorrr.views.block.{BlockConstants, HasBlockSizeProperty}
import app.logorrr.views.search.OpsToolBar
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

class DecreaseBlockSizeButton(val blockSizeProperty: SimpleIntegerProperty) extends
  RectButton(width = 8
    , height = 8
    , color = Color.GRAY
    , tooltipMessage = "decrease block size") with HasBlockSizeProperty {

  setOnAction(_ => {
    if (getBlockSize() - OpsToolBar.blockSizeStep > BlockConstants.MinSize) {
      setBlockSize(getBlockSize() - OpsToolBar.blockSizeStep)
    } else {
      setBlockSize(BlockConstants.MinSize)
    }
  })

}
