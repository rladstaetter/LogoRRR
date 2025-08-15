package app.logorrr.views.ops.time

import java.time.{Duration, Instant}

object TimeRange {

  lazy val defaultTimeRange = TimeRange(Instant.EPOCH, Instant.now())

}

case class TimeRange(start: Instant, end: Instant) {
  require(start.isBefore(end))

  /**
   * Checks if this TimeRange completely contains another TimeRange.
   * A TimeRange A contains TimeRange B if A.start < B.start and B.end < A.end.
   *
   * @param other The other TimeRange to check for containment.
   * @return True if this TimeRange contains the other TimeRange, false otherwise.
   */
  def contains(other: TimeRange): Boolean = {
    start.isBefore(other.start) && other.end.isBefore(end)
  }

  /**
   * Checks if this TimeRange contains a specific Instant.
   * An Instant is contained if this.start < instant and instant < this.end.
   *
   * @param instant The Instant to check for containment.
   * @return True if this TimeRange contains the Instant, false otherwise.
   */
  def contains(instant: Instant): Boolean = {
    start.isBefore(instant) && instant.isBefore(end)
  }

  // Calculate the duration of the TimeRange
  val duration: Duration = Duration.between(start, end)
}