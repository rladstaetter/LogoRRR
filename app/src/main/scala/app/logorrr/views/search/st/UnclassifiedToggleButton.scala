package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{BooleanProperty, ObjectPropertyBase}

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

  override def init(fileIdProperty: ObjectPropertyBase[FileId]
                    , visibleBinding: BooleanBinding
                    , mutSearchTerm: MutableSearchTerm
                    , activeProperty: BooleanProperty): Unit = {
    super.init(fileIdProperty, visibleBinding, mutSearchTerm, activeProperty)

  }

  override def shutdown(activeProperty: BooleanProperty): Unit = {
    super.shutdown(activeProperty)
  }

