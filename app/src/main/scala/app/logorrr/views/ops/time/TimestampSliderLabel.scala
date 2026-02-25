package app.logorrr.views.ops.time

import javafx.beans.binding.{BooleanBinding, ObjectBinding}
import javafx.beans.property.{DoubleProperty, SimpleObjectProperty}
import javafx.scene.control.Label

import java.time.format.DateTimeFormatter


class TimestampSliderLabel extends Label:

  def init(hasTimestampBinding: BooleanBinding
           , valueProperty: DoubleProperty
           , dateTimeFormatterProperty: ObjectBinding[DateTimeFormatter]): Unit = {
    visibleProperty().bind(hasTimestampBinding)
    textProperty.bind(new DateTimeFormatterBinding(valueProperty, dateTimeFormatterProperty))
  }

  def shutdown(): Unit =
    visibleProperty().unbind()
    textProperty.unbind()