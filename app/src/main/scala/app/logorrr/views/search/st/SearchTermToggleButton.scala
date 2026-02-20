package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.LogFilePredicate
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.{BooleanProperty, ObjectProperty, ObjectPropertyBase}
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList

import java.lang
import java.util.function.Predicate

class SearchTermToggleButton(entries: ObservableList[LogEntry]
                             , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]
                             , logFilePredicate: LogFilePredicate)
  extends ASearchTermToggleButton(entries, predicateProperty, logFilePredicate):

  override def init(fileIdProperty: ObjectPropertyBase[FileId], visibleBinding: BooleanBinding, mutSearchTerm: MutableSearchTerm, mutSearchTerms: ObservableList[MutableSearchTerm]): Unit = {
    super.init(fileIdProperty, visibleBinding, mutSearchTerm, mutSearchTerms)
    hitsProperty.bind(Bindings.createLongBinding(() => entries.stream().filter(t => t.value.contains(getValue)).count, entries))
  }

  override def shutdown(activeProperty: BooleanProperty): Unit = {
    super.shutdown(activeProperty)
    hitsProperty.unbind()
  }




