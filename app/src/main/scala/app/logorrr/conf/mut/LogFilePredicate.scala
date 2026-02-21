package app.logorrr.conf.mut

import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.property.{ObjectProperty, SimpleBooleanProperty, SimpleLongProperty}
import javafx.collections.ObservableList

import java.util.function.Predicate
import scala.jdk.CollectionConverters.*

object LogFilePredicate:

  def containsCondition(logEntry: LogEntry, mutSearchTerms: ObservableList[MutableSearchTerm]): Boolean =
    mutSearchTerms.stream.filter(_.isActive).map(_.getValue).anyMatch(needle => logEntry.value.contains(needle))

  def update(predicateProperty: ObjectProperty[Predicate[? >: LogEntry]], predicate: LogFilePredicate): Unit =
    predicateProperty.set(null)
    predicateProperty.set(predicate)


class LogFilePredicate(mutSearchTerms: ObservableList[MutableSearchTerm]) extends Predicate[LogEntry] {

  val showUnclassifiedProperty = new SimpleBooleanProperty(true)
  val lowerTimestampValueProperty = new SimpleLongProperty()
  val upperTimestampValueProperty = new SimpleLongProperty()

  override def test(logEntry: LogEntry): Boolean = {
    val timeCondition =
      (logEntry.someEpochMilli, Option(lowerTimestampValueProperty.get()), Option(upperTimestampValueProperty.get())) match
        case (Some(instant), Some(lower), Some(upper)) =>
          lower <= instant && instant <= upper
        case _ => true // if one of the conditions is not defined, return true
    val res = timeCondition && (showUnclassifiedProperty.get() || LogFilePredicate.containsCondition(logEntry, mutSearchTerms))
    res
  }
}
