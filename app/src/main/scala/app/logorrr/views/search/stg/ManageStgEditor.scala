package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Label, ListView}
import javafx.scene.layout.{Priority, VBox}

object StgListView extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgListView])
}

case class StgListView(fileId: FileId) extends ListView[StgEntry] {
  setId(StgListView.uiNode(fileId).value)
  itemsProperty.set(LogoRRRGlobals.getLogFileSettings(fileId).searchTermGroupEntries)
  // setMinHeight(200) // Give the list some height

  // Set the custom cell factory to add the 'X' delete button
  setCellFactory(_ => new StgEditorListviewCell(fileId))

}

case class ManageStgEditor(fileId: FileId) extends VBox(10) {
  VBox.setVgrow(this, Priority.ALWAYS)
  val existingGroupsLabel = new Label("Existing Groups")
  existingGroupsLabel.setStyle("-fx-font-weight: bold")
  val groupsListView = new StgListView(fileId)
  VBox.setVgrow(groupsListView, Priority.ALWAYS)
  //groupsListView.setPadding(new Insets(10))


  getChildren.addAll(existingGroupsLabel, groupsListView)
}
