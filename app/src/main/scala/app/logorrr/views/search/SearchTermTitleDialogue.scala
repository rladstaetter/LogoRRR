package app.logorrr.views.search

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.views.settings.timestamp.AlwaysGrowHorizontalRegion
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.VBox
import javafx.stage.{Modality, Stage}

class SearchTermTitleDialogue(searchTermToolbar: SearchTermToolBar
                              , contextMenu: SearchTermContextMenu) extends Stage {
  initOwner(null)
  initModality(Modality.WINDOW_MODAL)
  setTitle("Save Search Terms")

  val nameLabel = new Label("Enter a name for the search terms:")
  val nameField = new TextField()
  nameField.setPromptText("Enter a name...")
  val maxLength = 200

  // Constrain TextField length
  nameField.textProperty().addListener((_, oldValue, newValue) => {
    if (newValue.length > maxLength) {
      nameField.setText(oldValue)
    }
  })

  val saveButton = new Button("Save")
  saveButton.setOnAction(_ => {
    val name = nameField.getText()
    if (name.nonEmpty) {
      addToToolbar(name)
      addToContextMenu(name)
      LogoRRRGlobals.putSearchTerms(name, searchTermToolbar.activeSearchTerms())
      LogoRRRGlobals.persist()
      close()
    } else {
      println("Name cannot be empty.")
    }
  })

  private def addToContextMenu(name: String): Unit = {
    // if it is the first menu item, add a spacer
    if (contextMenu.getItems.size() == 1) {
      contextMenu.getItems.add(new SeparatorMenuItem)
    }
    val item = new MenuItem(name)
    contextMenu.getItems.add(item)
  }

  private def addToToolbar(name: String): Unit = {
    val spacer = new AlwaysGrowHorizontalRegion
    val label = new SearchTermTitleLabel(name)
    val indexOfLastElement = searchTermToolbar.getItems.size() - 1
    val lastItem = searchTermToolbar.getItems.get(indexOfLastElement)
    if (lastItem.isInstanceOf[SearchTermTitleLabel]) {
      searchTermToolbar.getItems.remove(indexOfLastElement)
      searchTermToolbar.getItems.add(label)
    } else {
      searchTermToolbar.getItems.addAll(spacer, label)
    }
  }

  val contentLayout = new VBox(10)
  contentLayout.getChildren.addAll(nameLabel, nameField, saveButton)
  contentLayout.setPadding(new javafx.geometry.Insets(10))

  setScene(new Scene(contentLayout, 300, 150))

}
