package app.logorrr.views.ops.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{Duration, Instant}

class TimeRangeSpec extends AnyWordSpec with Matchers {

  "TimeRange" should:

    "throw an IllegalArgumentException when start is after end" in:
      // Define Instant objects where start is after end
      val start = Instant.parse("2023-01-01T11:00:00Z")
      val end = Instant.parse("2023-01-01T10:00:00Z")

      // Expect an IllegalArgumentException to be thrown when creating the TimeRange
      a[IllegalArgumentException] should be thrownBy:
        TimeRange(start, end)

    "throw an IllegalArgumentException when start is equal to end" in:
      // Define Instant objects where start is equal to end
      val sameTime = Instant.parse("2023-01-01T10:00:00Z")

      // Expect an IllegalArgumentException to be thrown when creating the TimeRange
      a[IllegalArgumentException] should be thrownBy:
        TimeRange(sameTime, sameTime)

}