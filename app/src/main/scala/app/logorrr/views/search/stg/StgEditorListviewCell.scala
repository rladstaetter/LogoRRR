package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.st.SimpleSearchTermVis
import javafx.scene.control.{Button, Label, ListCell, ToolBar}
import javafx.scene.layout.{HBox, Priority, Region}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

object DeleteStgButton extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DeleteStgButton])
}

case class DeleteStgButton(fileId: FileId) extends Button {
  setId(DeleteStgButton.uiNode(fileId).value)
  setGraphic(new FontIcon(FontAwesomeRegular.WINDOW_CLOSE))
}

class StgEditorListviewCell(fileId: FileId) extends ListCell[StgEntry] {

  val deleteButton = DeleteStgButton(fileId)


  override def updateItem(item: StgEntry, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      val label = new Label(item.name)
      label.setPrefWidth(100)

      // Action when the 'X' button is clicked
      deleteButton.setOnAction(_ => {
        // get the item for this cell
        val itemToRemove = getItem
        if (itemToRemove != null) {
          // Remove the item from the ObservableList
          LogoRRRGlobals.getLogFileSettings(fileId).removeSearchTermGroup(itemToRemove.name)
        }
      })


      // Use a region to push the button to the right
      val filler = new Region()
      HBox.setHgrow(filler, Priority.ALWAYS)
      val vis: Seq[SimpleToggleButton] = item.terms.map(t => new SimpleToggleButton(SimpleSearchTermVis(t)))

      val toolBar = new ToolBar
      toolBar.getItems.addAll(deleteButton, label)
      toolBar.getItems.addAll(vis: _*)
      setGraphic(toolBar)
    }
  }
}
