package app.logorrr.views.search


import app.logorrr.io.FileId
import app.logorrr.jfxbfr.MutFilter
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.filter.RegexFilter
import app.logorrr.views.search.predicates.ContainsPredicate
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
                   , addFilterFn: MutFilter[_] => Unit) extends Button {

  setId(SearchButton.uiNode(fileId).value)
  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  setOnAction(_ => {
    if (searchTextField.getText.nonEmpty) {
      val filter: MutFilter[_] =
        if (regexToggleButton.isSelected) {
          RegexFilter(searchTextField.getText, colorPicker.getValue, active = true)
        } else {
          MutFilter(ContainsPredicate(searchTextField.getText), colorPicker.getValue, active = true)
        }
      colorPicker.setValue(JfxUtils.randColor)
      searchTextField.clear()
      addFilterFn(filter)
    }
  })

}
