package app.logorrr.conf

import app.logorrr.util.TimeUtil
import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import scala.util.{Random, Try}

object TimeSettingsSpec:
  val gen: Gen[TimeSettings] = for
    start <- Gen.posNum[Int]
    end <- Gen.chooseNum(start, Random.nextInt(start + 100) + start)
    dtp <- Gen.const(TimeSettings.DefaultPattern)
  yield TimeSettings(start, end, dtp)


class TimeSettingsSpec extends AnyWordSpec:

  "Log Format Tests" should :
    "parse valid timestamp" in :
      val t: Option[Instant] = TimeUtil.parseInstant("2024-08-11 12:38:00", TimeSettings(0, 19, "yyyy-MM-dd HH:mm:ss"))
      assert(t.isDefined)
      assert(t.exists(_.toEpochMilli == 1723372680000L))
    "Instant.MIN.toEpocMilli throws exception" in assert(Try(Instant.MIN.toEpochMilli).isFailure)
    "Instant.MAX.toEpocMilli throws exception" in assert(Try(Instant.MAX.toEpochMilli).isFailure)
    "compare Instant.MIN with itself" in assert(Instant.MIN == Instant.MIN && Instant.MIN.equals(Instant.MIN))
    "compare Instant.MAX with itself" in assert(Instant.MAX == Instant.MAX && Instant.MAX.equals(Instant.MAX))
