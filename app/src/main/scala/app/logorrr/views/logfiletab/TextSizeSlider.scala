package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Slider

object TextSizeSlider extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TextSizeSlider])

class TextSizeSlider extends Slider:
  setMin(TextConstants.MinFontSize)
  setMax(TextConstants.MaxFontSize)
  setMajorTickUnit(TextConstants.fontSizeStep)
  setMinorTickCount(0)
  setSnapToTicks(true)
  setShowTickLabels(false)
  setShowTickMarks(false)

  def bind(fileProperty: SimpleObjectProperty[FileId]): Unit =
    idProperty.bind(Bindings.createObjectBinding(() => TextSizeSlider.uiNode(fileProperty.get()).value, fileProperty))

  def unbind(): Unit = idProperty.unbind()
