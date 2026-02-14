package app.logorrr.views.autoscroll

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectPropertyBase, Property}
import javafx.scene.control.{CheckBox, Tooltip}

object AutoScrollCheckBox extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[AutoScrollCheckBox])


class AutoScrollCheckBox extends CheckBox with BoundId(AutoScrollCheckBox.uiNode(_).value):
  setTooltip(new Tooltip("if enabled, LogoRRR observes file for changes"))

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , autoscrollActiveProperty: Property[java.lang.Boolean]): Unit =
    bindIdProperty(fileIdProperty)
    selectedProperty.bindBidirectional(autoscrollActiveProperty)

  def shutdown(autoscrollActiveProperty: Property[java.lang.Boolean]): Unit =
    unbindIdProperty()
    selectedProperty().unbindBidirectional(autoscrollActiveProperty)