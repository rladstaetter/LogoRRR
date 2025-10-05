package app.logorrr.views.autoscroll

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.model.HasFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{CheckBox, Tooltip}

object AutoScrollCheckBox extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[AutoScrollCheckBox])

}

class AutoScrollCheckBox(val fileId: FileId) extends CheckBox with HasFileId {
  setId(AutoScrollCheckBox.uiNode(fileId).value)
  setTooltip(new Tooltip("autoscroll"))
  selectedProperty().bindBidirectional(LogoRRRGlobals.getLogFileSettings(fileId).autoScrollActiveProperty)

}
