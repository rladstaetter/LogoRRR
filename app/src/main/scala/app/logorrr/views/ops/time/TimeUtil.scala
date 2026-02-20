package app.logorrr.views.ops.time

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList

import java.time.Instant

object TimeUtil:

  /**
   * Given an observable list of log entries, calculate the min and max instant. LogEntries doesn't have to be ordered.
   *
   * @param logEntries list of log entries
   * @return min and max Instant of all log entries
   */
  def calcTimeInfo(logEntries: ObservableList[LogEntry]): Option[TimeRange] =
    if !logEntries.isEmpty then

      var minInstant = Long.MaxValue
      var maxInstant = Long.MinValue
      logEntries.forEach((e: LogEntry) => {
        e.someEpochMilli match {
          case Some(instant) =>
            if instant < minInstant then
              minInstant = instant
            if instant > maxInstant then
              maxInstant = instant
          case None => // do nothing
        }
      })
      if minInstant == Long.MinValue || minInstant == Long.MaxValue then
        None
      else if maxInstant == Long.MinValue || maxInstant == Long.MaxValue then
        None
      else if minInstant.equals(maxInstant) then
        None
      else
        Option(TimeRange(minInstant, maxInstant))
    else None
