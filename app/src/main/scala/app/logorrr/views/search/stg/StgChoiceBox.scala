package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.property.SimpleListProperty
import javafx.scene.control.{ChoiceBox, Tooltip}

object StgChoiceBox extends UiNodeFileIdAware {
  val style = "-fx-pref-width: 150;"

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgChoiceBox])

}

class StgChoiceBox(fileId: FileId, searchTerms: SimpleListProperty[MutableSearchTerm]) extends ChoiceBox[String] {
  setId(StgChoiceBox.uiNode(fileId).value)
  setStyle(StgChoiceBox.style)
  setTooltip(new Tooltip("shows search term groups"))

  getSelectionModel.selectedItemProperty.addListener(JfxUtils.onNew[String](groupName => {
    LogoRRRGlobals.getSearchTerms(groupName) match {
      case Some(selectedTerms) =>
        searchTerms.clear()
        searchTerms.addAll(selectedTerms.map(MutableSearchTerm.apply): _*)
      case None => // do nothing
    }

  }))

  def add(searchTermGroup: String): Unit = {
    getItems.add(searchTermGroup)
  }

}


