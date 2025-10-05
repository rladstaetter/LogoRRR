package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.views.search.SearchTermGroupEntry
import app.logorrr.views.search.searchterm.SimpleSearchTermVis
import javafx.scene.control.{Button, Label, ListCell, ToolBar}
import javafx.scene.layout.{HBox, Priority, Region}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

class EditSearchGroupNameCell extends ListCell[SearchTermGroupEntry] {


  override def updateItem(item: SearchTermGroupEntry, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      val label = new Label(item.name)
      label.setPrefWidth(100)
      val deleteButton = new Button()
      deleteButton.setGraphic(new FontIcon(FontAwesomeRegular.WINDOW_CLOSE))
      val toolBar = new ToolBar

      // Action when the 'X' button is clicked
      deleteButton.setOnAction(_ => {
        // get the item for this cell
        val itemToRemove = getItem
        if (itemToRemove != null) {
          // Remove the item from the ObservableList
          LogoRRRGlobals.removeSearchTermGroup(itemToRemove.name)
        }
      })


      // Use a region to push the button to the right
      val filler = new Region()
      HBox.setHgrow(filler, Priority.ALWAYS)
      toolBar.getItems.addAll(deleteButton, label)
      val vis: Seq[SimpleToggleButton] = item.terms.map(t => new SimpleToggleButton(SimpleSearchTermVis(t)))
      toolBar.getItems.addAll(vis: _*)
      setGraphic(toolBar)
    }
  }
}
