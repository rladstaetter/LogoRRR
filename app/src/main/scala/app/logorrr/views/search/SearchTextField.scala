package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.{HashUtil, OsUtil}
import app.logorrr.views.LogoRRRNode
import javafx.beans.binding.StringBinding
import javafx.scene.control.{TextField, Tooltip}

object SearchTextField {

  def id(id: FileId): LogoRRRNode = LogoRRRNode("searchtextfield-" + HashUtil.md5Sum(id))
}

class SearchTextField(fileId: FileId
                      , regexToggleButton: SearchActivateRegexToggleButton)
  extends TextField {

  setId(SearchTextField.id(fileId).value)
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
