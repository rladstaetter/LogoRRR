package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import javafx.scene.control.Button
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.stage.Stage

object CreateStgOrCloseEditorUi {

}

class CreateStgOrCloseEditorUi(fileId: FileId, stage: Stage, addFn: String => Unit) extends VBox {

  private val createButton = CreateStgButton(fileId)
  private val nameField = new StgNameTextField(fileId, createButton.fire)
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

  private val closeButton: Button = new CloseStgEditorButton(fileId, stage)

  val hBox = {
    val filler = new Region()
    HBox.setHgrow(filler, Priority.ALWAYS)
    val h = new HBox()
    h.getChildren.addAll(nameField, createButton, filler, closeButton)
    h
  }
  getChildren.add(hBox)
}
