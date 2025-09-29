package app.logorrr.views.search

import app.logorrr.util.JfxUtils
import javafx.scene.control._

object SearchTermContextMenu {

  val toggleGroup = new ToggleGroup()

  toggleGroup.selectedToggleProperty.addListener(JfxUtils.onNew[Toggle](
    newToggle => {
      Option(newToggle) match {
        case Some(selectedItem: RadioMenuItem) =>
          System.out.println("Selected search term group: " + selectedItem.getText)
        case _ =>
      }
    }))


}


/**
 * Encodes a context menu for the search term ToolBar
 *
 * It provides means to save the current search term
 */
class SearchTermContextMenu() extends ContextMenu {

  def add(searchTermGroupName: String): Unit = {
    // if it is the first menu item, add a spacer
    if (getItems.size() == 1) {
      getItems.add(new SeparatorMenuItem)
    }
    val item = new RadioMenuItem(searchTermGroupName)
    item.setToggleGroup(SearchTermContextMenu.toggleGroup)
    item.setSelected(true)
    getItems.add(item)
  }


}
