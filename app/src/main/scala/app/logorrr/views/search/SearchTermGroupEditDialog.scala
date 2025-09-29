package app.logorrr.views.search

import app.logorrr.conf.LogoRRRGlobals
import javafx.collections.ObservableList
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.stage.{Modality, Stage}

/*
class SearchTermGroupEditDialog(addFn: String => Unit) extends Stage {
  initModality(Modality.WINDOW_MODAL)
  setTitle("Create search term group")

  val items: ObservableList[String] = LogoRRRGlobals.searchTermGroupNames

  val nameLabel = new Label("Enter a a search group name:")
  val saveButton = new Button("Create or update group")

  val buttonWrapper = new HBox()
  buttonWrapper.setAlignment(Pos.CENTER_RIGHT)
  buttonWrapper.getChildren.add(saveButton)

  val nameField = new SearchTermGroupNameTextField(saveButton.fire)

  saveButton.setOnAction(_ => {
    val searchTermGroupName = nameField.getText()
    if (searchTermGroupName.nonEmpty) {
      addFn(searchTermGroupName)
      close()
    } else {
      println("Name cannot be empty.")
    }
  })

  private val contentLayout = new VBox(10)
  contentLayout.setPadding(new javafx.geometry.Insets(10))
  contentLayout.getChildren.addAll(nameLabel, nameField, buttonWrapper)

  setScene(new Scene(contentLayout, 300, 130))

}
*/

class SearchTermGroupEditDialog(addFn: String => Unit) extends Stage {
  initModality(Modality.WINDOW_MODAL)
  setTitle("Edit search term groups")

  // The ObservableList of items (Group names)
  val items: ObservableList[String] = LogoRRRGlobals.searchTermGroupNames

  // --- UI Components for New Group Creation ---
  val newNameLabel = new Label("Create New Group:")
  val createButton = new Button("Create Group")
  val nameField = new SearchTermGroupNameTextField(createButton.fire) // Reusing existing text field

  // Disable the create button initially if the text field is empty
  createButton.disableProperty().bind(nameField.textProperty().isEmpty)

  // --- UI Component for Deletion (ListView) ---
  val existingGroupsLabel = new Label("Existing Groups:")
  val groupsListView = new ListView[String]()
  groupsListView.itemsProperty.set(LogoRRRGlobals.searchTermGroupNames)
  groupsListView.setPrefHeight(150) // Give the list some height

  // Set the custom cell factory to add the 'X' delete button
  groupsListView.setCellFactory(_ => new EditSearchGroupNameCell())

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

  // Optional: Add a close button
  val closeButton = new Button("Close")
  closeButton.setOnAction(_ => close())

  // --- Layout ---

  // Layout for new group creation
  private val createLayout = new VBox(5)
  createLayout.getChildren.addAll(newNameLabel, nameField, createButton)

  // Layout for existing groups (including list and close button)
  private val existingGroupsLayout = new VBox(5)
  existingGroupsLayout.getChildren.addAll(existingGroupsLabel, groupsListView)

  // Button Wrapper for Close Button
  val closeButtonWrapper = new HBox(closeButton)
  closeButtonWrapper.setAlignment(Pos.CENTER_RIGHT)

  private val contentLayout = new VBox(10)
  contentLayout.setPadding(new Insets(10))
  // Combined layout: Create area, List View, Close button
  contentLayout.getChildren.addAll(createLayout, new Separator(), existingGroupsLayout, closeButtonWrapper)

  // --- Final Setup ---
  setScene(new Scene(contentLayout, 350, 400)) // Adjusted size for list view
}


class EditSearchGroupNameCell extends ListCell[String] {

  override def updateItem(item: String, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      val label = new Label(item)
      val deleteButton = new Button("X")
      val hBox = new HBox(5)
      // Set a uniform size for the button
      deleteButton.setPrefSize(20, 20)
      deleteButton.setMinSize(20, 20)
      deleteButton.setMaxSize(20, 20)

      // Set a fixed alignment for the HBox content
      hBox.setAlignment(Pos.CENTER_RIGHT)
      hBox.getChildren.add(deleteButton)

      // Action when the 'X' button is clicked
      deleteButton.setOnAction(_ => {
        // get the item for this cell
        val itemToRemove = getItem
        if (itemToRemove != null) {
          // Remove the item from the ObservableList
          LogoRRRGlobals.removeSearchTermGroup(itemToRemove)
        }
      })

      // Set the graphic (the 'X' button) to the right of the text
      hBox.getChildren.set(0, deleteButton)
      setGraphic(hBox)

      // Use a region to push the button to the right
      val filler = new Region()
      HBox.setHgrow(filler, Priority.ALWAYS)
      hBox.getChildren.clear()
      hBox.getChildren.addAll(label, filler, deleteButton)
      setGraphic(hBox)
    }
  }
}