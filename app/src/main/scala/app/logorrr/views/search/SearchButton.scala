package app.logorrr.views.search


import app.logorrr.io.FileId
import app.logorrr.jfxbfr.Fltr
import app.logorrr.util.JfxUtils
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object SearchButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchButton])

}

class SearchButton(fileId: FileId
                   , searchTextField: SearchTextField
                   , regexToggleButton: SearchActivateRegexToggleButton
                   , colorPicker: SearchColorPicker
                   , addFilterFn: Fltr => Unit) extends Button {

  setId(SearchButton.uiNode(fileId).value)
  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  setOnAction(_ => {
    if (searchTextField.getText.nonEmpty) {
      val filter: Fltr =
        if (regexToggleButton.isSelected) {
          RegexFilter(searchTextField.getText, colorPicker.getValue, active = true)
        } else {
          Fltr(searchTextField.getText, colorPicker.getValue, active = true)
        }
      colorPicker.setValue(JfxUtils.randColor)
      searchTextField.clear()
      addFilterFn(filter)
    }
  })

}
