package app.logorrr.views.text

import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.ListBinding
import javafx.beans.property.SimpleListProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.paint.Color

/**
 * Converts a list of mutable search terms to a list of (String, Color) pairs
 *
 * Filters all active search terms and extracts value and color
 *
 * @param mutSearchTerms the list of searchterms
 */
class MutSearchTermBinding(mutSearchTerms: SimpleListProperty[MutableSearchTerm]) extends ListBinding[(String, Color)] {
  this.bind(mutSearchTerms)

  override def computeValue(): ObservableList[(String, Color)] = {
    val list = FXCollections.observableArrayList[(String, Color)]()
    mutSearchTerms.filtered(e => e.isActive).forEach(st => list.add((st.getValue, st.getColor)))
    list
  }
}
