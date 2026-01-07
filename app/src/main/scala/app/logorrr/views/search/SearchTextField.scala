package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{TextField, Tooltip}
import net.ladstatt.util.os.OsUtil


object SearchTextField extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchTextField])

}

class SearchTextField(fileId: FileId) extends TextField {

  setId(SearchTextField.uiNode(fileId).value)
  setPrefWidth(200)
  setMaxWidth(200)
  setTooltip(new Tooltip(s"enter search pattern\n\nshortcut: ${OsUtil.osFun("CTRL-F", "COMMAND-F", "CTRL-F")}"))
  setPromptText("<search string>")

}
