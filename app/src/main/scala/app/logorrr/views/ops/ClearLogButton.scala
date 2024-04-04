package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.util.HashUtil
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object ClearLogButton extends UiNodeAware {

  def uiNode(id: FileId): UiNode = UiNode("clearlogbutton-" + HashUtil.md5Sum(id))

}

class ClearLogButton(id: FileId, logEntries: ObservableList[LogEntry]) extends Button {
  private val icon = new FontIcon(FontAwesomeSolid.TRASH)

  setId(ClearLogButton.uiNode(id).value)
  setGraphic(icon)
  setTooltip(new Tooltip("clear log file"))
  setOnAction(_ => {
    logEntries.clear()
  })
}
