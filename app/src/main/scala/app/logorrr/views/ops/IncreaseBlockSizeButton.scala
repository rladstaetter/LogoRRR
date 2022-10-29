package app.logorrr.views.ops

import app.logorrr.views.block.HasBlockSizeProperty
import app.logorrr.views.search.OpsToolBar
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

class IncreaseBlockSizeButton(val blockSizeProperty: SimpleIntegerProperty) extends
  RectButton(2 * OpsToolBar.blockSizeStep
    , 2 * OpsToolBar.blockSizeStep
    , Color.GRAY
    , "increase block size") with HasBlockSizeProperty {

  setOnAction(_ => {
    if (getBlockSize() + OpsToolBar.blockSizeStep < 10 * OpsToolBar.blockSizeStep) {
      setBlockSize(getBlockSize() + OpsToolBar.blockSizeStep)
    }
  })

}
