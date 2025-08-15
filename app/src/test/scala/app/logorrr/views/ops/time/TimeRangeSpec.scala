package app.logorrr.views.ops.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{Duration, Instant}

// Unit test for the TimeRange case class
class TimeRangeSpec extends AnyWordSpec with Matchers {

  "TimeRange" should {

    "be created successfully when start is strictly before end" in {
      // Define valid Instant objects for testing
      val start = Instant.parse("2023-01-01T10:00:00Z")
      val end = Instant.parse("2023-01-01T11:00:00Z")

      // Attempt to create a TimeRange with valid inputs
      val timeRange = TimeRange(start, end)

      // Assert that the TimeRange was created and its properties match the inputs
      timeRange.start shouldBe start
      timeRange.end shouldBe end
    }

    "throw an IllegalArgumentException when start is after end" in {
      // Define Instant objects where start is after end
      val start = Instant.parse("2023-01-01T11:00:00Z")
      val end = Instant.parse("2023-01-01T10:00:00Z")

      // Expect an IllegalArgumentException to be thrown when creating the TimeRange
      a[IllegalArgumentException] should be thrownBy {
        TimeRange(start, end)
      }
    }

    "throw an IllegalArgumentException when start is equal to end" in {
      // Define Instant objects where start is equal to end
      val sameTime = Instant.parse("2023-01-01T10:00:00Z")

      // Expect an IllegalArgumentException to be thrown when creating the TimeRange
      a[IllegalArgumentException] should be thrownBy {
        TimeRange(sameTime, sameTime)
      }
    }

    "correctly calculate its duration" in {
      val start = Instant.parse("2023-01-01T10:00:00Z")
      val end = Instant.parse("2023-01-01T11:30:00Z") // 1 hour 30 minutes
      val timeRange = TimeRange(start, end)

      // The expected duration is 1 hour and 30 minutes
      timeRange.duration shouldBe Duration.ofMinutes(90)
    }

    "correctly check if it contains another TimeRange" in {
      val outerRange = TimeRange(Instant.parse("2023-01-01T09:00:00Z"), Instant.parse("2023-01-01T17:00:00Z"))

      // Case 1: Inner range fully contained
      val innerRange = TimeRange(Instant.parse("2023-01-01T10:00:00Z"), Instant.parse("2023-01-01T16:00:00Z"))
      outerRange.contains(innerRange) shouldBe true

      // Case 2: Inner range starts at outer range's start (not strictly contained)
      val startsAtOuter = TimeRange(Instant.parse("2023-01-01T09:00:00Z"), Instant.parse("2023-01-01T10:00:00Z"))
      outerRange.contains(startsAtOuter) shouldBe false

      // Case 3: Inner range ends at outer range's end (not strictly contained)
      val endsAtOuter = TimeRange(Instant.parse("2023-01-01T16:00:00Z"), Instant.parse("2023-01-01T17:00:00Z"))
      outerRange.contains(endsAtOuter) shouldBe false

      // Case 4: Overlaps partially at start
      val overlapsStart = TimeRange(Instant.parse("2023-01-01T08:00:00Z"), Instant.parse("2023-01-01T10:00:00Z"))
      outerRange.contains(overlapsStart) shouldBe false

      // Case 5: Overlaps partially at end
      val overlapsEnd = TimeRange(Instant.parse("2023-01-01T16:00:00Z"), Instant.parse("2023-01-01T18:00:00Z"))
      outerRange.contains(overlapsEnd) shouldBe false

      // Case 6: Completely outside
      val outsideRange = TimeRange(Instant.parse("2023-01-01T18:00:00Z"), Instant.parse("2023-01-01T19:00:00Z"))
      outerRange.contains(outsideRange) shouldBe false
    }

    "correctly check if it contains an Instant" in {
      val timeRange = TimeRange(Instant.parse("2023-01-01T10:00:00Z"), Instant.parse("2023-01-01T12:00:00Z"))

      // Case 1: Instant within the range
      val instantInside = Instant.parse("2023-01-01T11:00:00Z")
      timeRange.contains(instantInside) shouldBe true

      // Case 2: Instant at the start boundary (not strictly contained)
      val instantAtStart = Instant.parse("2023-01-01T10:00:00Z")
      timeRange.contains(instantAtStart) shouldBe false

      // Case 3: Instant at the end boundary (not strictly contained)
      val instantAtEnd = Instant.parse("2023-01-01T12:00:00Z")
      timeRange.contains(instantAtEnd) shouldBe false

      // Case 4: Instant before the range
      val instantBefore = Instant.parse("2023-01-01T09:00:00Z")
      timeRange.contains(instantBefore) shouldBe false

      // Case 5: Instant after the range
      val instantAfter = Instant.parse("2023-01-01T13:00:00Z")
      timeRange.contains(instantAfter) shouldBe false
    }
  }
}