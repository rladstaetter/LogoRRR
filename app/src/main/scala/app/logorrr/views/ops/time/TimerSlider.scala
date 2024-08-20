package app.logorrr.views.ops.time

import app.logorrr.model.LogEntry
import javafx.beans.binding.BooleanBinding
import javafx.collections.ObservableList
import javafx.scene.control.{Slider, Tooltip}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

object TimerSlider {

  def format(epochMilli: Long, formatter: DateTimeFormatter): String = {
    val instant = Instant.ofEpochMilli(epochMilli)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault)
    dateTime.format(formatter)
  }

  /**
   * Given an observable list of log entries, calculate the min and max instant. LogEntries doesn't have to be ordered.
   *
   * @param logEntries list of log entries
   * @return min and max Instant of all log entries
   */
  def calculateBoundaries(logEntries: ObservableList[LogEntry]): Option[(Instant, Instant)] = {
    if (!logEntries.isEmpty) {
      var minInstant = Instant.MAX
      var maxInstant = Instant.MIN
      logEntries.forEach((e: LogEntry) => {
        e.someInstant match {
          case Some(instant) =>
            if (instant.isBefore(minInstant)) {
              minInstant = instant
            }
            if (instant.isAfter(maxInstant)) {
              maxInstant = instant
            }
          case None => // do nothing
        }
      })
      if (minInstant == Instant.MIN || minInstant == Instant.MAX) {
        None
      } else if (maxInstant == Instant.MIN || maxInstant == Instant.MAX) {
        None
      } else Option((minInstant, maxInstant))
    } else None
  }

}

class TimerSlider(hasLogEntrySettingsBinding: BooleanBinding, tooltipText: String) extends Slider {
  visibleProperty().bind(hasLogEntrySettingsBinding)
  disableProperty().bind(hasLogEntrySettingsBinding.not)
  setTooltip(new Tooltip(tooltipText))
  setPrefWidth(400)

}