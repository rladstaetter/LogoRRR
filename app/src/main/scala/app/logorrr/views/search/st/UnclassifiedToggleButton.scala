package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{BooleanProperty, ObjectPropertyBase, StringProperty}
import javafx.scene.control.Label
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color

import java.util
import java.util.function.Predicate

class UnclassifiedPredicate(selectedProperty: BooleanProperty, activeSearchTerms: util.HashSet[String]) extends Predicate[LogEntry] {
  override def test(t: LogEntry): Boolean = {
    val value = t.value
    selectedProperty.get() && !activeSearchTerms.stream.anyMatch(needle => {
      value.contains(needle)
    })
  }
}


class UnclassifiedToggleButton extends ASearchTermToggleButton:

  searchTermLabel.setMinHeight(36)
  setGraphic(searchTermLabel)


