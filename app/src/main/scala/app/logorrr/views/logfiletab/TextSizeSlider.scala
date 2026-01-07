package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.Slider

object TextConstants {

  /** increment / decrement font size */
  val fontSizeStep: Int = 1

  val DefaultFontSize = 12
  val MinFontSize = 3
  val MaxFontSize = 30
}

object TextSizeSlider extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TextSizeSlider])
}

class TextSizeSlider(fileId: FileId) extends Slider {
  setId(TextSizeSlider.uiNode(fileId).value)
  setMin(TextConstants.MinFontSize)
  setMax(TextConstants.MaxFontSize)
  setMajorTickUnit(TextConstants.fontSizeStep)
  setMinorTickCount(0)
  setSnapToTicks(true)
  setShowTickLabels(false)
  setShowTickMarks(false)
}