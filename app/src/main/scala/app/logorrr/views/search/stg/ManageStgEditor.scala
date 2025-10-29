package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import javafx.scene.layout.{Priority, VBox}


case class ManageStgEditor(fileId: FileId) extends VBox(10) {
  VBox.setVgrow(this, Priority.ALWAYS)

  val groupsListView = new StgListView(fileId)
  VBox.setVgrow(groupsListView, Priority.ALWAYS)

  getChildren.addAll(groupsListView)
}
