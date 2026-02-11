package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Slider

object TextSizeSlider extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TextSizeSlider])

class TextSizeSlider extends Slider with BoundFileId(TextSizeSlider.uiNode(_).value):
  setMin(TextConstants.MinFontSize)
  setMax(TextConstants.MaxFontSize)
  setMajorTickUnit(TextConstants.fontSizeStep)
  setMinorTickCount(0)
  setSnapToTicks(true)
  setShowTickLabels(false)
  setShowTickMarks(false)

