package app.logorrr.views.search

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object SearchButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchButton])


class SearchButton(fileId: FileId
                   , searchTextField: SearchTextField
                   , colorPicker: SearchColorPicker
                   , addFilterFn: MutableSearchTerm => Unit) extends Button:

  setId(SearchButton.uiNode(fileId).value)
  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  setOnAction:
    _ =>
      if searchTextField.getText.nonEmpty then
        val term = SearchTerm(searchTextField.getText, colorPicker.getValue, active = true)
        addFilterFn(MutableSearchTerm(term))
        resetColorPickerAndSearchField()


  private def resetColorPickerAndSearchField(): Unit =
    colorPicker.setValue(JfxUtils.randColor)
    searchTextField.clear()
