package app.logorrr.conf

import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import scala.util.Try

object TimestampSettingsSpec:
  val gen: Gen[TimestampSettings] = for
    sr <- SimpleRangeSpec.gen
    dtp <- Gen.const(TimestampSettings.DefaultPattern)
  yield TimestampSettings(sr, dtp)


class TimestampSettingsSpec extends AnyWordSpec:

  "Log Format Tests" should:
    "parse valid timestamp" in:
      val t: Option[Instant] = TimestampSettings.parseInstant("2024-08-11 12:38:00", TimestampSettings(SimpleRange(0, 19), "yyyy-MM-dd HH:mm:ss"))
      assert(t.isDefined)
      assert(t.exists(_.toEpochMilli == 1723372680000L))
    "Instant.MIN.toEpocMilli throws exception" in assert(Try(Instant.MIN.toEpochMilli).isFailure)
    "Instant.MAX.toEpocMilli throws exception" in assert(Try(Instant.MAX.toEpochMilli).isFailure)
    "compare Instant.MIN with itself" in assert(Instant.MIN == Instant.MIN && Instant.MIN.equals(Instant.MIN))
    "compare Instant.MAX with itself" in assert(Instant.MAX == Instant.MAX && Instant.MAX.equals(Instant.MAX))
