package app.logorrr.views.settings

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutSearchTermGroup
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.Label
import javafx.scene.layout.{Priority, VBox}

class SettingsManageStgEditor(fileId: FileId, entries: SimpleListProperty[MutSearchTermGroup]) extends VBox(10):
  VBox.setVgrow(this, Priority.ALWAYS)
  val title = new Label("Favorites")
  title.setStyle("-fx-font-weight: bold")
  // only show nonempty entries

  val groupsListView = new SettingsStgListView(entries)
  VBox.setVgrow(groupsListView, Priority.ALWAYS)

  getChildren.addAll(title, groupsListView)


  def shutdown() : Unit = groupsListView.shutdown()