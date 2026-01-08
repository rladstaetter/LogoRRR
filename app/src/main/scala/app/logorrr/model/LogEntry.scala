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
                    , someInstant: Option[Instant]
                    , someDurationSinceFirstInstant: Option[Duration]):

  def someJfxDuration: Option[util.Duration] = someDurationSinceFirstInstant.map(d => javafx.util.Duration.millis(d.toMillis.toDouble))

  /** returns a copy of this log entry without timestamp information */
  def withOutTimestamp(): LogEntry = copy(someInstant = None, someDurationSinceFirstInstant = None)

