package app.logorrr.views.search.stg

import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTermGroup}
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectPropertyBase
import javafx.collections.ObservableList
import javafx.scene.control.ListView


object StgListView extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgListView])

class StgListView(fileId: FileId) extends ListView[SearchTermGroup] with BoundId(StgListView.uiNode(_).value):
  setItems(LogoRRRGlobals.getLogFileSettings(fileId).searchTermGroupEntries)

  setCellFactory(_ => new StgEditorListViewCell(fileId))

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , searchTermGroupEntries: ObservableList[SearchTermGroup]): Unit = {
    setItems(searchTermGroupEntries)
    bindIdProperty(fileIdProperty)
  }

  def shutdown(): Unit = {
    getItems.clear()
    unbindIdProperty()
  }


