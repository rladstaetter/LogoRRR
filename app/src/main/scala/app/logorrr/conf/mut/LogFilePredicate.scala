package app.logorrr.conf.mut

import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.property.{SimpleBooleanProperty, SimpleLongProperty}
import javafx.collections.ObservableList

import java.util.function.Predicate
import scala.jdk.CollectionConverters.*

class LogFilePredicate(mutSearchTerms: ObservableList[MutableSearchTerm]) extends Predicate[LogEntry] {

  val showUnclassifiedProperty = new SimpleBooleanProperty()
  val lowerTimestampValueProperty = new SimpleLongProperty()
  val upperTimestampValueProperty = new SimpleLongProperty()

  def getSearchTerms: Set[String] = mutSearchTerms.asScala.filter(_.isActive).map(_.getValue).toSet

  override def test(t: LogEntry): Boolean = {
    val timeCondition =
      (t.someInstant, Option(lowerTimestampValueProperty.get()), Option(upperTimestampValueProperty.get())) match
        case (Some(instant), Some(lower), Some(upper)) =>
          val i = instant.toEpochMilli
          lower <= i && i <= upper
        case _ => true // if one of the conditions is not defined, return true
    val containsCondition = getSearchTerms.exists(needle => t.value.contains(needle))
    val showUnclassified = showUnclassifiedProperty.get() && !containsCondition
    val res = timeCondition && (showUnclassified || containsCondition)
    // println(timeCondition.toString + " && (" + showUnclassified + "||" + containsCondition + ") " + getSearchTerms + " -> " + res)
    res
  }
}
