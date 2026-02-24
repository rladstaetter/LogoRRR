package app.logorrr.views.ops.time

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList

import java.util.Optional

object TimeUtil:

  /**
   * Given an observable list of log entries, calculate the min and max instant. LogEntries doesn't have to be ordered.
   *
   * @param logEntries list of log entries
   * @return min and max Instant of all log entries
   */
  def calcTimeInfo(logEntries: ObservableList[LogEntry]): Option[TimeRange] = {

    if !logEntries.isEmpty then
      val first: Optional[LogEntry] = logEntries.stream.dropWhile(e => e.someEpochMilli.isEmpty).findFirst()
      val last: Optional[LogEntry] = logEntries.reversed().stream.dropWhile(e => e.someEpochMilli.isEmpty).findFirst()

      var (minInstant, maxInstant) =
        if (first.isPresent && last.isPresent) then
          (first.get().someEpochMilli.get, last.get().someEpochMilli.get)
        else
          (Long.MaxValue, Long.MinValue)

      if minInstant == Long.MinValue || minInstant == Long.MaxValue then
        None
      else if maxInstant == Long.MinValue || maxInstant == Long.MaxValue then
        None
      else if minInstant.equals(maxInstant) then
        None
      else
        Option(TimeRange(minInstant, maxInstant))
    else None
  }
