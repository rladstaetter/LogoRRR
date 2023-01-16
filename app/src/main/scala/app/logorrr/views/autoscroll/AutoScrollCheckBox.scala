package app.logorrr.views.autoscroll

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogIdAware
import javafx.scene.control.{CheckBox, Tooltip}


class AutoScrollCheckBox(val pathAsString: String) extends CheckBox with LogIdAware {
  setTooltip(new Tooltip("autoscroll"))
  selectedProperty().bindBidirectional(LogoRRRGlobals.getLogFileSettings(pathAsString).autoScrollProperty)


}
