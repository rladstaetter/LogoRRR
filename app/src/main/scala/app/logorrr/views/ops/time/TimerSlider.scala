package app.logorrr.views.ops.time

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import javafx.scene.control.Slider

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

object TimerSlider {
  def format(epochMilli: Long, formatter: DateTimeFormatter): String = {
    val instant = Instant.ofEpochMilli(epochMilli)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault)
    dateTime.format(formatter)
  }
}

class TimerSlider(filteredList: ObservableList[LogEntry]) extends Slider {
/*
  def onChange(c: ListChangeListener.Change[_ <: LogEntry]): Unit = {
    while (c.next()) {
      updateMinMax()
    }
  } */

  private def updateMinMax(): Unit = {
    if (2 <= filteredList.size) {
      var minInstant = Instant.MAX
      var maxInstant = Instant.MIN
      filteredList.forEach((e: LogEntry) => {
        e.someInstant match {
          case Some(instant) =>
            if (instant.isBefore(minInstant)) {
              minInstant = instant
            }
            if (instant.isAfter(maxInstant)) {
              maxInstant = instant
            }
          case None =>
        }
      })
      setMin(minInstant.toEpochMilli.toDouble)
      setMax(maxInstant.toEpochMilli.toDouble)
    }
  }

  setPrefWidth(500)
  updateMinMax()
 // filteredList.addListener(JfxUtils.mkListChangeListener(onChange))


}