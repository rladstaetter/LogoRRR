package app.logorrr.views.ops.time

import java.time.{Duration, Instant}

case class TimeInfo(start: Instant, end: Instant) {
  val duration: Duration = Duration.between(start, end)
}
