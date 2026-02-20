package app.logorrr.views.ops.time

import java.time.Instant

object TimeRange:

  def apply(a: Instant, b: Instant) = new TimeRange(a.toEpochMilli, b.toEpochMilli)

  lazy val defaultTimeRange = TimeRange(Long.MinValue, Long.MaxValue)

case class TimeRange(min: Long, max: Long):
  require(min < max)

