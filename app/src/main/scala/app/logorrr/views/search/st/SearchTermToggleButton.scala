package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.{BooleanProperty, ObjectPropertyBase}
import javafx.collections.ObservableList

import java.util.function.Predicate

class SearchTermToggleButton(entries: ObservableList[LogEntry]) extends ASearchTermToggleButton:

  override def init(fileIdProperty: ObjectPropertyBase[FileId]
                    , visibleBinding: BooleanBinding
                    , mutSearchTerm: MutableSearchTerm
                    , activeProperty: BooleanProperty): Unit = {
    super.init(fileIdProperty, visibleBinding, mutSearchTerm, activeProperty)
    hitsProperty.bind(Bindings.createLongBinding(() => entries.stream().filter(t => t.value.contains(getValue)).count, entries))
  }

  override def shutdown(activeProperty: BooleanProperty): Unit = {
    super.shutdown(activeProperty)
    hitsProperty.unbind()
  }




