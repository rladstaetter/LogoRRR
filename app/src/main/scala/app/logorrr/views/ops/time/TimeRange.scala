package app.logorrr.views.ops.time

import java.time.Instant

object TimeRange:

  def apply(a: Instant, b: Instant) = new TimeRange(a.toEpochMilli, b.toEpochMilli)

  lazy val defaultTimeRange: (Long, Long) = (0L, System.currentTimeMillis())

case class TimeRange(min: Long, max: Long):
  require(min < max)

