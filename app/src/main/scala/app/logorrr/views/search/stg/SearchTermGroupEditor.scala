package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.{Priority, VBox}
import javafx.stage.{Modality, Stage}

object SearchTermGroupEditor {

}

class SearchTermGroupEditor(fileId: FileId, addFn: String => Unit) extends Stage {
  initModality(Modality.WINDOW_MODAL)
  setTitle("Edit search term groups")

  // Layout for existing groups (including list and close button)
  private val manageExistingSearchTermGroup = new ManageStgEditor(fileId)

  private val createStgOrCloseEditor = new CreateStgOrCloseEditorUi(fileId, this, addFn)

  private val contentLayout = new VBox(10)
  VBox.setVgrow(contentLayout, Priority.ALWAYS)
  contentLayout.setPadding(new Insets(10))

  contentLayout.getChildren.addAll(manageExistingSearchTermGroup, createStgOrCloseEditor)

  // --- Final Setup ---
  setScene(new Scene(contentLayout, 800, 600)) // Adjusted size for list view
}
