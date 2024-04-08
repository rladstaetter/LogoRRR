package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.OsUtil
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.beans.binding.StringBinding
import javafx.scene.control.{TextField, Tooltip}



object SearchTextField extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchTextField])

}

class SearchTextField(fileId: FileId
                      , regexToggleButton: SearchActivateRegexToggleButton)
  extends TextField {

  setId(SearchTextField.uiNode(fileId).value)
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
