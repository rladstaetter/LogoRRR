package app.logorrr.views.autoscroll

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.{LogEntry, LogIdAware}
import app.logorrr.util.JfxUtils
import javafx.collections.ObservableList
import javafx.scene.control.{CheckBox, ContextMenu, MenuItem, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

class ClearLogMenuItem(logEntries: ObservableList[LogEntry]) extends MenuItem("clear log") {
  setOnAction(_ => {
    logEntries.clear()
  })
  setGraphic(new FontIcon(FontAwesomeSolid.TRASH))
}

class AutoScrollCheckBox(val pathAsString: String
                         , logEntries: ObservableList[LogEntry]) extends CheckBox with LogIdAware {
  setTooltip(new Tooltip("autoscroll"))
  val cm = new ContextMenu(new ClearLogMenuItem(logEntries))
  selectedProperty().bindBidirectional(LogoRRRGlobals.getLogFileSettings(pathAsString).autoScrollProperty)

  selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean]({
    selected =>
      if (selected) {
        if (Option(getContextMenu).isEmpty) {
          setContextMenu(cm)
        }
      } else {
        setContextMenu(null)
      }
  }))
}
