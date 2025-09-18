package app.logorrr.views.search

import app.logorrr.conf.LogoRRRGlobals
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.{HBox, VBox}
import javafx.stage.{Modality, Stage}


class SearchTermTitleDialogue(searchTermToolbar: SearchTermToolBar
                              , choiceBox: SearchTermGroupChoiceBox) extends Stage {
  initOwner(choiceBox.getScene.getWindow)
  initModality(Modality.WINDOW_MODAL)
  setTitle("Save Search Terms")

  val nameLabel = new Label("Enter a name for the search terms:")
  val saveButton = new Button("Save")

  val buttonWrapper = new HBox()
  buttonWrapper.setAlignment(Pos.CENTER_RIGHT)
  buttonWrapper.getChildren.add(saveButton)

  val nameField = new SearchTermGroupNameTextField(saveButton.fire)

  saveButton.setOnAction(_ => {
    val searchTermGroupName = nameField.getText()
    if (searchTermGroupName.nonEmpty) {
      addNewSearchTermGroup(searchTermToolbar, choiceBox, searchTermGroupName)
      close()
    } else {
      println("Name cannot be empty.")
    }
  })

  private def addNewSearchTermGroup(searchTermToolbar: SearchTermToolBar
                                    , choiceBox: SearchTermGroupChoiceBox
                                    , searchTermGroup: String): Unit = {
    searchTermToolbar.addSearchGroupName(searchTermGroup)
    choiceBox.add(searchTermGroup)
    LogoRRRGlobals.putSearchTerms(searchTermGroup, searchTermToolbar.activeSearchTerms())
    LogoRRRGlobals.persist()
  }

  val contentLayout = new VBox(10)
  contentLayout.setPadding(new javafx.geometry.Insets(10))
  contentLayout.getChildren.addAll(nameLabel, nameField, buttonWrapper)

  setScene(new Scene(contentLayout, 300, 130))

}
