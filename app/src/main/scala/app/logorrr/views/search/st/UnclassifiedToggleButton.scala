package app.logorrr.views.search.st

import app.logorrr.conf.mut.LogFilePredicate
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.property.{BooleanProperty, ObjectProperty}
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList

import java.lang
import java.util.function.Predicate

class UnclassifiedPredicate(selectedProperty: BooleanProperty, mutSearchTerms: ObservableList[MutableSearchTerm]) extends Predicate[LogEntry] {
  override def test(t: LogEntry): Boolean =
    selectedProperty.get() && !LogFilePredicate.containsCondition(t, mutSearchTerms)
}


class UnclassifiedToggleButton(entries: ObservableList[LogEntry]
                               , mutSearchTerms: ObservableList[MutableSearchTerm]
                               , unclassifiedProperty: BooleanProperty
                               , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]
                               , logFilePredicate: LogFilePredicate)
  extends ASearchTermToggleButton(entries):

  resetPredicate()

  // predicate has to change every time, thats why 'new ...' is used
  def resetPredicate() : Unit = setPredicate(new UnclassifiedPredicate(selectedProperty, mutSearchTerms))

  protected val updateChangeListener: ChangeListener[lang.Boolean] = JfxUtils.onNew[java.lang.Boolean](e => {
    if selectedProperty().get() then
      unclassifiedProperty.set(true)
    else unclassifiedProperty.set(false)
    predicateProperty.set(null)
    predicateProperty.set(logFilePredicate)
  })
