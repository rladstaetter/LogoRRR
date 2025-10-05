package app.logorrr.views.search.stg

import app.logorrr.views.search.SearchTermGroupNameTextField
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.stage.Stage

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
