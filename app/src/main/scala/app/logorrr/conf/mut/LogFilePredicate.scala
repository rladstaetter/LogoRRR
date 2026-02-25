package app.logorrr.conf.mut

import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.property.{ObjectProperty, SimpleBooleanProperty, SimpleLongProperty}
import javafx.collections.ObservableList

import java.util.function.Predicate

object LogFilePredicate:

  def containsCondition(logEntry: LogEntry, mutSearchTerms: ObservableList[MutableSearchTerm]): Boolean =
    mutSearchTerms.stream.filter(_.isActive).map(_.getValue).anyMatch(needle => logEntry.value.contains(needle))

  def update(predicateProperty: ObjectProperty[Predicate[? >: LogEntry]], predicate: LogFilePredicate): Unit =
    predicateProperty.set(null) //  necessary to retrigger filter operation
    predicateProperty.set(predicate)


class LogFilePredicate(mutSearchTerms: ObservableList[MutableSearchTerm]
                       , lowerBoundery: SimpleLongProperty
                       , upperBoundary: SimpleLongProperty
                      ) extends Predicate[LogEntry] {

  val showUnclassifiedProperty = new SimpleBooleanProperty(true)

  override def test(logEntry: LogEntry): Boolean = {
    val timeCondition =
      (logEntry.someEpochMilli, lowerBoundery.get(), upperBoundary.get()) match
        case (Some(instant), lower, upper) =>
          lower <= instant && instant <= upper
        case _ => true // if one of the conditions is not defined, return true
    timeCondition && (showUnclassifiedProperty.get() || LogFilePredicate.containsCondition(logEntry, mutSearchTerms))
  }
}
