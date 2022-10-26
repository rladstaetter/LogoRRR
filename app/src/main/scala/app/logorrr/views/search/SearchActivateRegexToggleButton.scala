package app.logorrr.views.search

import app.logorrr.util.{LogoRRRFonts, OsUtil}
import javafx.scene.control.{Label, ToggleButton, Tooltip}
import javafx.scene.paint.Color

class SearchActivateRegexToggleButton
  extends ToggleButton {
  private val label = new Label("r")
  label.setStyle(LogoRRRFonts.jetBrainsMono(8))
  label.setTextFill(Color.DARKGREY)
  setGraphic(label)
  setTooltip(new Tooltip(
    s"""activate regular expression search
       |
       |shortcut: ${OsUtil.osFun("CTRL-R", "COMMAND-R")}""".stripMargin))

}
