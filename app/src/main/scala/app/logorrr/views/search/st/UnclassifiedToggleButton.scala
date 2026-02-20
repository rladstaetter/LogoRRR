package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.LogFilePredicate
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{BooleanProperty, ObjectProperty, ObjectPropertyBase}
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList

import java.util.function.Predicate
import java.{lang, util}

class UnclassifiedPredicate(selectedProperty: BooleanProperty, activeSearchTerms: util.HashSet[String]) extends Predicate[LogEntry] {
  override def test(t: LogEntry): Boolean = {
    val value = t.value
    selectedProperty.get() && !activeSearchTerms.stream.anyMatch(needle => {
      value.contains(needle)
    })
  }
}


class UnclassifiedToggleButton(entries: ObservableList[LogEntry]
                               , mutSearchTerms: ObservableList[MutableSearchTerm]
                               , unclassifiedProperty: BooleanProperty
                               , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]
                               , logFilePredicate: LogFilePredicate)
  extends ASearchTermToggleButton(entries, predicateProperty, logFilePredicate):

  override def init(fileIdProperty: ObjectPropertyBase[FileId], visibleBinding: BooleanBinding, mutSearchTerm: MutableSearchTerm, mutSearchTerms: ObservableList[MutableSearchTerm]): Unit = {
    super.init(fileIdProperty, visibleBinding, mutSearchTerm, mutSearchTerms)
    logFilePredicate.showUnclassifiedProperty.bind(selectedProperty())
  }

  override def shutdown(activeProperty: BooleanProperty): Unit = {
    super.shutdown(activeProperty)
    logFilePredicate.showUnclassifiedProperty.unbind()
  }

