package app.logorrr.views.logfiletab

import app.logorrr.views.block.BlockConstants
import javafx.scene.control.Slider

class BlockSizeSlider extends Slider {
  getStylesheets.add(getClass.getResource("/app/logorrr/Slider.css").toExternalForm)
  setMin(BlockConstants.MinBlockSize)
  setMax(BlockConstants.MaxBlockSize)
  setShowTickLabels(false)
  setShowTickMarks(false)
}
