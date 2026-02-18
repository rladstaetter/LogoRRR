package app.logorrr.conf.mut

import app.logorrr.model.LogEntry
import javafx.beans.property.{SimpleBooleanProperty, SimpleLongProperty, SimpleObjectProperty, SimpleSetProperty}
import javafx.collections.{FXCollections, ObservableSet}

import java.time.Instant
import java.util.function.Predicate
import scala.jdk.CollectionConverters.*

class LogFilePredicate extends Predicate[LogEntry] {

  val searchtermsProperty = new SimpleSetProperty[String](FXCollections.observableSet())
  val showUnclassifiedProperty = new SimpleBooleanProperty()
  val lowerTimestampValueProperty = new SimpleLongProperty()
  val upperTimestampValueProperty = new SimpleLongProperty()

  override def test(t: LogEntry): Boolean = {
    lazy val timeCondition =
      (t.someInstant, Option(lowerTimestampValueProperty.get()), Option(upperTimestampValueProperty.get())) match
        case (Some(instant), Some(lower), Some(upper)) =>
          val i = instant.toEpochMilli
          lower <= i && i <= upper
        case _ => true // if one of the conditions is not defined, return true
    val containsCondition = searchtermsProperty.get().asScala.exists(needle => t.value.contains(needle))
    val showUnclassified = showUnclassifiedProperty.get() && !containsCondition
    timeCondition && (showUnclassified || containsCondition)
  }
}
