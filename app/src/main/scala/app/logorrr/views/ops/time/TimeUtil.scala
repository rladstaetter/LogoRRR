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
      var minInstant = Instant.MAX
      var maxInstant = Instant.MIN
      logEntries.forEach((e: LogEntry) => {
        e.someInstant match {
          case Some(instant) =>
            if instant.isBefore(minInstant) then {
              minInstant = instant
            }
            if instant.isAfter(maxInstant) then {
              maxInstant = instant
            }
          case None => // do nothing
        }
      })
      if minInstant == Instant.MIN || minInstant == Instant.MAX then
        None
      else if maxInstant == Instant.MIN || maxInstant == Instant.MAX then
        None
      else if minInstant.equals(maxInstant) then
        None
      else
        Option(TimeRange(minInstant, maxInstant))
    else None
