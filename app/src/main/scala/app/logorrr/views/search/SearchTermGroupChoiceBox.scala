package app.logorrr.views.search

import app.logorrr.util.JfxUtils
import javafx.scene.control.ChoiceBox

object SearchTermGroupChoiceBox {
  val Empty = ""
  val SaveAsText = "Save as ..."
}

class SearchTermGroupChoiceBox(searchTermToolBar: SearchTermToolBar) extends ChoiceBox[String] {
  getItems.addAll(SearchTermGroupChoiceBox.Empty, SearchTermGroupChoiceBox.SaveAsText)
  setValue(SearchTermGroupChoiceBox.Empty)

  setStyle("-fx-font-size: 20pt; -fx-pref-width: 150;")

  getSelectionModel.selectedItemProperty.addListener(JfxUtils.onNew[String](newValue => {
    if (newValue == SearchTermGroupChoiceBox.SaveAsText) {
      val saveStage = new SearchTermTitleDialogue(searchTermToolBar, this)
      saveStage.showAndWait()
    }
  }))

  def add(searchTermGroup: String): Unit = {
    getItems.add(searchTermGroup)
  }


}

