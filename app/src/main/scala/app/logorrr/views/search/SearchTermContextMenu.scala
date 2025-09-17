package app.logorrr.views.search

import javafx.scene.control._

class SearchTermTitleLabel(text: String) extends Label(text) {
  setStyle("-fx-font-weight: bold; -fx-font-size: 20pt; -fx-opacity: 0.5;")
}


object SearchTermContextMenu {

  class SaveAsMenuItem(searchTermToolbar: SearchTermToolBar, contextMenu: SearchTermContextMenu) extends MenuItem("Save as ...") {
    setOnAction(_ => {
      val saveStage = new SearchTermTitleDialogue(searchTermToolbar, contextMenu)
      saveStage.show()
    })

  }

}


/**
 * Encodes a context menu for the search term ToolBar
 *
 * It provides means to save the current search term
 */
class SearchTermContextMenu(searchTermToolbar: SearchTermToolBar) extends ContextMenu {

  val saveAsItem = new SearchTermContextMenu.SaveAsMenuItem(searchTermToolbar, this)

  getItems.addAll(saveAsItem)


}
