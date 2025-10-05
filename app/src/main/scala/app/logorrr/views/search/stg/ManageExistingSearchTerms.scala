package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.views.search.SearchTermGroupEntry
import javafx.scene.control.{Label, ListView}
import javafx.scene.layout.{Priority, VBox}

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
