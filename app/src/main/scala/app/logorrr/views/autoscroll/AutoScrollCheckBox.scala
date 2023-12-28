package app.logorrr.views.autoscroll

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.model.HasFileId
import javafx.scene.control.{CheckBox, Tooltip}


class AutoScrollCheckBox(val fileId: FileId) extends CheckBox with HasFileId {
  setTooltip(new Tooltip("autoscroll"))
  selectedProperty().bindBidirectional(LogoRRRGlobals.getLogFileSettings(fileId).autoScrollActiveProperty)


}
