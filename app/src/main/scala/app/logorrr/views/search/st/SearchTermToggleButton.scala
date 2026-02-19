package app.logorrr.views.search.st

import app.logorrr.conf.mut.LogFilePredicate
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import javafx.beans.property.{ObjectProperty, SimpleSetProperty}
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList

import java.lang
import java.util.function.Predicate

class SearchTermToggleButton(entries: ObservableList[LogEntry]
                             , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]
                             , logFilePredicate: LogFilePredicate) extends ASearchTermToggleButton(entries):

  setPredicate(IsActiveAndContainsPredicate(this))

  protected val updateChangeListener: ChangeListener[lang.Boolean] = JfxUtils.onNew[java.lang.Boolean](e => {
    predicateProperty.set(null)
    predicateProperty.set(logFilePredicate)
  })



