package app.logorrr.views.search.stg

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.views.search.st.SimpleSearchTermVis
import javafx.scene.control.ToolBar
import net.ladstatt.util.log.CanLog


class CreateStgUi(mutLogFileSettings: MutLogFileSettings
                  , fileId: FileId
                  , activeSearchTerms: Seq[SearchTerm]) extends ToolBar with CanLog {

  private val createButton = CreateStgButton(fileId)
  private val nameField = new StgNameTextField(fileId, createButton.fire)
  createButton.disableProperty().bind(nameField.textProperty().isEmpty)

  // --- Event Handling (Create Button) ---
  createButton.setOnAction(_ => {
    val searchTermGroupName = nameField.getText()
    if (searchTermGroupName.nonEmpty) {
      // addFn(searchTermGroupName)
      mutLogFileSettings.putSearchTerms(searchTermGroupName, activeSearchTerms)
      nameField.clear() // Clear the field after creation
      // Do not close, allow the user to create/delete more
    } else {
      logTrace("Name cannot be empty.")
    }
  })

  private val searchTermVis: Seq[SimpleToggleButton] = activeSearchTerms.map(s => new SimpleToggleButton(SimpleSearchTermVis(s)))

  getItems.addAll(Seq(createButton, nameField) ++ searchTermVis: _*)
}
