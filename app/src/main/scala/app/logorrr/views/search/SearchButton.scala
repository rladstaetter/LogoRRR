package app.logorrr.views.search


import app.logorrr.util.ColorUtil
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

class SearchButton(searchTextField: SearchTextField
                   , regexToggleButton: SearchActivateRegexToggleButton
                   , colorPicker: SearchColorPicker
                   , addFilterFn: Filter => Unit) extends Button {
  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  setOnAction(_ => {
    if (searchTextField.getText.nonEmpty) {
      val filter =
        if (regexToggleButton.isSelected) {
          new RegexFilter(searchTextField.getText, colorPicker.getValue)
        } else {
          new Filter(searchTextField.getText, colorPicker.getValue)
        }
      colorPicker.setValue(ColorUtil.randColor)
      searchTextField.clear()
      addFilterFn(filter)
    }
  })

}
