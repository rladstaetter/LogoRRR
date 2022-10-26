package app.logorrr.views.search

import app.logorrr.util.OsUtil
import javafx.scene.control.{TextField, Tooltip}

class SearchTextField extends TextField {
  setPrefWidth(200)
  setMaxWidth(200)
  setTooltip(new Tooltip(s"enter search pattern\n\nshortcut: ${OsUtil.osFun("CTRL-F", "COMMAND-F","CTRL-F")}"))

}
