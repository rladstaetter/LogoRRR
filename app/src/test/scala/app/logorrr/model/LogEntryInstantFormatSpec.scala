package app.logorrr.model

import app.logorrr.views.settings.timestamp.SimpleRange
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import scala.util.Try

class LogEntryInstantFormatSpec extends AnyWordSpec {

  "Log Format Tests" should {
    "parse valid timestamp" in {
      val t: Option[Instant] = LogEntryInstantFormat.parseInstant("2024-08-11 12:38:00", LogEntryInstantFormat(SimpleRange(0, 19), "yyyy-MM-dd HH:mm:ss"))
      assert(t.isDefined)
      t.foreach(x => println(x.toEpochMilli))
      assert(t.exists(_.toEpochMilli == 1723372680000L))
    }
    "Instant.MIN.toEpocMilli throws exception" in assert(Try(Instant.MIN.toEpochMilli).isFailure)
    "Instant.MAX.toEpocMilli throws exception" in assert(Try(Instant.MAX.toEpochMilli).isFailure)
    "compare Instant.MIN with itself" in assert(Instant.MIN == Instant.MIN && Instant.MIN.equals(Instant.MIN))
    "compare Instant.MAX with itself" in assert(Instant.MAX == Instant.MAX && Instant.MAX.equals(Instant.MAX))
  }
}
