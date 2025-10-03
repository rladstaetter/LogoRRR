package app.logorrr.views.search

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.views.search.searchterm.SimpleSearchTermVis
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.stage.{Modality, Stage}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


class CreateSearchTerm(stage: Stage, addFn: String => Unit) extends VBox {

  // --- UI Components for New Group Creation ---
  val newNameLabel = new Label("Create New Group")
  newNameLabel.setStyle("-fx-font-weight: bold")

  val hBox = new HBox()
  val createButton = new Button("Create")
  val nameField = new SearchTermGroupNameTextField(createButton.fire)
  createButton.disableProperty().bind(nameField.textProperty().isEmpty)

  // --- Event Handling (Create Button) ---
  createButton.setOnAction(_ => {
    val searchTermGroupName = nameField.getText()
    if (searchTermGroupName.nonEmpty) {
      addFn(searchTermGroupName)
      nameField.clear() // Clear the field after creation
      // Do not close, allow the user to create/delete more
    } else {
      println("Name cannot be empty.")
    }
  })

  private val closeButton: Button = {
    val b = new Button("Close")
    b.setOnAction(_ => stage.close())
    b
  }
  val filler = new Region()
  HBox.setHgrow(filler, Priority.ALWAYS)
  hBox.getChildren.addAll(nameField, createButton, filler, closeButton)
  getChildren.add(hBox)
}

class ManageExistingSearchTerms extends VBox(10) {
  VBox.setVgrow(this, Priority.ALWAYS)
  val existingGroupsLabel = new Label("Existing Groups")
  existingGroupsLabel.setStyle("-fx-font-weight: bold")
  val groupsListView = new ListView[SearchTermGroupEntry]()
  VBox.setVgrow(groupsListView, Priority.ALWAYS)
  //groupsListView.setPadding(new Insets(10))
  groupsListView.itemsProperty.set(LogoRRRGlobals.searchTermGroupEntries)
  groupsListView.setMinHeight(200) // Give the list some height

  // Set the custom cell factory to add the 'X' delete button
  groupsListView.setCellFactory(_ => new EditSearchGroupNameCell())

  getChildren.addAll(existingGroupsLabel, groupsListView)
}

class SearchTermGroupEditDialog(addFn: String => Unit) extends Stage {
  initModality(Modality.WINDOW_MODAL)
  setTitle("Edit search term groups")

  // Layout for existing groups (including list and close button)
  private val manageExistingSearchTermGroup = new ManageExistingSearchTerms

  private val createOrCloseSearchTermGroup = new CreateSearchTerm(this, addFn)

  private val contentLayout = new VBox(10)
  VBox.setVgrow(contentLayout, Priority.ALWAYS)
  contentLayout.setPadding(new Insets(10))

  contentLayout.getChildren.addAll(manageExistingSearchTermGroup, createOrCloseSearchTermGroup)

  // --- Final Setup ---
  setScene(new Scene(contentLayout, 800, 600)) // Adjusted size for list view
}


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

class SimpleToggleButton(sstv: SimpleSearchTermVis) extends ToggleButton {
  setPrefWidth(100)
  setGraphic(sstv)
  setStyle(ColorUtil.mkCssBackgroundString(sstv.colorProperty.get()))
  setSelected(true)
}