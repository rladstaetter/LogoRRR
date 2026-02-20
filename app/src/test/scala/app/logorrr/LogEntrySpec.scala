package app.logorrr

import app.logorrr.conf.TimestampSettings
import app.logorrr.model.LogEntry
import javafx.scene.paint.Color
import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}

object LogEntrySpec:

  val genColor: Gen[Color] = for
    red <- Gen.choose(0.0, 1.0)
    green <- Gen.choose(0.0, 1.0)
    blue <- Gen.choose(0.0, 1.0)
    alpha <- Gen.choose(0.0, 1.0)
  yield Color.color(red, green, blue, alpha)

  val gen: Gen[LogEntry] =
    for
      l <- Gen.posNum[Int]
      value <- Gen.alphaStr
      someInstant <- Gen.const(None)
    yield LogEntry(l, value, someInstant)


class LogEntrySpec extends AnyWordSpecLike:

  "LogEntry" should:
    "show datetimeformatter usage" in:
      // see https://stackoverflow.com/questions/25229124/unsupportedtemporaltypeexception-when-formatting-instant-to-string
      val dtf: DateTimeFormatter = TimestampSettings.DefaultFormatter
        .withZone(ZoneId.of("+1"))
      //.withZone(ZoneId.systemDefault())
      val i: Instant = LocalDateTime.parse("""2022-02-15 16:37:14.374""", dtf).toInstant(ZoneOffset.of("+1"))
      assert(i.toString == "2022-02-15T15:37:14.374Z") // utc
      assert(dtf.format(i) == "2022-02-15 16:37:14.374") // local time zone
