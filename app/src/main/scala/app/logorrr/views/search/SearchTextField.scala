package app.logorrr.views.search

import app.logorrr.util.OsUtil
import javafx.beans.binding.StringBinding
import javafx.scene.control.{TextField, Tooltip}

class SearchTextField(regexToggleButton: SearchActivateRegexToggleButton) extends TextField {
  setPrefWidth(200)
  setMaxWidth(200)
  setTooltip(new Tooltip(s"enter search pattern\n\nshortcut: ${OsUtil.osFun("CTRL-F", "COMMAND-F", "CTRL-F")}"))

  promptTextProperty().bind(new StringBinding {
    bind(regexToggleButton.selectedProperty())

    override def computeValue(): String =
      if (regexToggleButton.isSelected) {
        "<regex search string>"
      } else {
        "<search string>"
      }
  })

}
