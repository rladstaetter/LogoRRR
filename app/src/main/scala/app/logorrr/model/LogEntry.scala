package app.logorrr.model


import javafx.util

import java.time.{Duration, Instant}

/**
 * represents one line in a log file
 *
 * @param lineNumber  line number of this log entry
 * @param value       contens of line in plaintext
 * @param someInstant a timestamp if there is any
 * */
case class LogEntry(lineNumber: Int
                    , value: String
                    , someEpochMilli: Option[Long]):

  lazy val someInstant = someEpochMilli.map(Instant.ofEpochMilli)

  /** returns a copy of this log entry without timestamp information */
  def withOutTimestamp(): LogEntry = copy(someEpochMilli = None)

