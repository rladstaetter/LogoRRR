package app.logorrr.views.search.stg

import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.ListView


object StgListView extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgListView])
}

case class StgListView(fileId: FileId) extends ListView[SearchTermGroup] {
  setId(StgListView.uiNode(fileId).value)
  setItems(LogoRRRGlobals.getLogFileSettings(fileId).searchTermGroupEntries)
  // setMinHeight(200) // Give the list some height

  // Set the custom cell factory to add the 'X' delete button
  setCellFactory(_ => new StgEditorListViewCell(fileId))

}




