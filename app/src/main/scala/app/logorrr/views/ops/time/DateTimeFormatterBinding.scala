package app.logorrr.views.ops.time

import javafx.beans.binding.StringBinding
import javafx.beans.property.{DoubleProperty, SimpleObjectProperty}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

class DateTimeFormatterBinding(valueProperty: DoubleProperty
                               , dateTimeFormatter: SimpleObjectProperty[DateTimeFormatter]) extends StringBinding {
  bind(valueProperty, dateTimeFormatter)

  override def computeValue(): String =
    Option(dateTimeFormatter.get) match
      case Some(value) =>
        val instant = Instant.ofEpochMilli(valueProperty.get.longValue())
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault)
        dateTime.format(value)
      case None => ""

}
