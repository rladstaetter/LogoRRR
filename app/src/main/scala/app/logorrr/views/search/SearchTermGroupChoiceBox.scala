package app.logorrr.views.search

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.util.JfxUtils
import app.logorrr.views.MutableSearchTerm
import javafx.beans.property.SimpleListProperty
import javafx.scene.control.{ChoiceBox, Tooltip}

object SearchTermGroupChoiceBox {
  val style = "-fx-pref-width: 150;"
}

class SearchTermGroupChoiceBox(searchTerms: SimpleListProperty[MutableSearchTerm]) extends ChoiceBox[String] {
  setStyle(SearchTermGroupChoiceBox.style)
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


