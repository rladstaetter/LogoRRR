package app.logorrr.views.search.stg

import app.logorrr.conf.{FileId, SearchTermGroup}
import javafx.beans.property.ObjectPropertyBase
import javafx.collections.ObservableList
import javafx.scene.layout.{Priority, VBox}


class ManageStgEditor(fileId: FileId) extends VBox(10):
  VBox.setVgrow(this, Priority.ALWAYS)
  val groupsListView = new StgListView(fileId)
  VBox.setVgrow(groupsListView, Priority.ALWAYS)

  getChildren.addAll(groupsListView)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , searchTermGroupEntries: ObservableList[SearchTermGroup]): Unit =
    groupsListView.init(fileIdProperty, searchTermGroupEntries)

  def shutdown(): Unit =
    groupsListView.shutdown()