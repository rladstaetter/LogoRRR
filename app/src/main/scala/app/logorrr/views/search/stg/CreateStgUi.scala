package app.logorrr.views.search.stg

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, SearchTerm}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.ToolBar
import net.ladstatt.util.log.TinyLog


class CreateStgUi(mutLogFileSettings: MutLogFileSettings
                  , fileId: FileId
                  , activeSearchTerms: Seq[SearchTerm]) extends ToolBar with TinyLog:

  private val createButton = new CreateStgButton
  private val nameField = new StgNameTextField(fileId, createButton.fire)
  createButton.disableProperty().bind(nameField.textProperty().isEmpty)

  // --- Event Handling (Create Button) ---
  createButton.setOnAction:
    _ =>
      val searchTermGroupName = nameField.getText()
      if searchTermGroupName.nonEmpty then
        // addFn(searchTermGroupName)
        mutLogFileSettings.putSearchTerms(searchTermGroupName, activeSearchTerms)
        nameField.clear() // Clear the field after creation
      // Do not close, allow the user to create/delete more
      else
        logTrace("Name cannot be empty.")


  private val searchTermVis: Seq[SearchTermLabel] = activeSearchTerms.map(s => SearchTermLabel(s))

  getItems.addAll(Seq(createButton, nameField) ++ searchTermVis *)

  def init(fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    createButton.init(fileIdProperty)
    nameField.init(fileIdProperty)

  def shutdown(): Unit =
    createButton.shutdown()
    nameField.shutdown()