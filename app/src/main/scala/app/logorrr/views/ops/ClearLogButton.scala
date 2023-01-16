package app.logorrr.views.ops

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

class ClearLogButton(logEntries: ObservableList[LogEntry]) extends Button {
  private val icon = new FontIcon(FontAwesomeSolid.TRASH)
  setGraphic(icon)
  setTooltip(new Tooltip("clear log file"))
  setOnAction(_ => {
    logEntries.clear()
  })
}
