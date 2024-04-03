package app.logorrr.views.search


import app.logorrr.io.FileId
import app.logorrr.util.{ColorUtil, HashUtil}
import app.logorrr.views.LogoRRRNode
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object SearchButton {

  def id(id: FileId): LogoRRRNode = LogoRRRNode("searchbutton-" + HashUtil.md5Sum(id))

}

class SearchButton(fileId: FileId
                   , searchTextField: SearchTextField
                   , regexToggleButton: SearchActivateRegexToggleButton
                   , colorPicker: SearchColorPicker
                   , addFilterFn: Filter => Unit) extends Button {

  setId(SearchButton.id(fileId).value)
  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  setOnAction(_ => {
    if (searchTextField.getText.nonEmpty) {
      val filter =
        if (regexToggleButton.isSelected) {
          new RegexFilter(searchTextField.getText, colorPicker.getValue, true)
        } else {
          new Filter(searchTextField.getText, colorPicker.getValue, true)
        }
      colorPicker.setValue(ColorUtil.randColor)
      searchTextField.clear()
      addFilterFn(filter)
    }
  })

}
