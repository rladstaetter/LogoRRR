package app.logorrr.views.search.stg

import app.logorrr.conf.FileId
import javafx.scene.layout.{Priority, VBox}


class ManageStgEditor(fileId: FileId) extends VBox(10):
  VBox.setVgrow(this, Priority.ALWAYS)

  val groupsListView = new StgListView(fileId)
  VBox.setVgrow(groupsListView, Priority.ALWAYS)

  getChildren.addAll(groupsListView)
