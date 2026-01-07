package app.logorrr.views.settings

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.stg.{SearchTermGroup, StgListView}
import javafx.collections.ObservableList
import javafx.scene.control.ListView


object SettingsStgListView extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgListView])
}

case class SettingsStgListView(fileId: FileId, entries: ObservableList[SearchTermGroup]) extends ListView[SearchTermGroup] {
  setId(SettingsEditor.SettingsStgListView.value)
  setItems(entries)
  // setMinHeight(200) // Give the list some height

  // Set the custom cell factory to add the 'X' delete button
  setCellFactory(_ => new SettingsStgListViewCell)

}