package app.logorrr.views.settings

import app.logorrr.conf.{FileId, SearchTermGroup}
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.layout.{Priority, VBox}

class SettingsManageStgEditor(fileId: FileId, entries: ObservableList[SearchTermGroup]) extends VBox(10):
  VBox.setVgrow(this, Priority.ALWAYS)
  val title = new Label("Global Search Term Groups")
  title.setStyle("-fx-font-weight: bold")
  val groupsListView = new SettingsStgListView(fileId, entries)
  VBox.setVgrow(groupsListView, Priority.ALWAYS)

  getChildren.addAll(title, groupsListView)
