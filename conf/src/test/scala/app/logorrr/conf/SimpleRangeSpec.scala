package app.logorrr.conf

import org.scalacheck.{Gen, Prop}
import org.scalactic.source
import org.scalactic.source.Position
import org.scalatest.wordspec.AnyWordSpec
import pureconfig.ConfigSource

object SimpleRangeSpec {

  val gen: Gen[SimpleRange] = for {
    start <- Gen.posNum[Int]
    end <- Gen.posNum[Int].map(i => i + start)
  } yield SimpleRange(start, end)

}


class SimpleRangeSpec extends CheaterSpec {

  "de/serialize" in {
    check(Prop.forAll(SimpleRangeSpec.gen) {
      sr =>
        ConfigSource.string(SimpleRange.writer.to(sr).render(RenderOptions.opts)).load[SimpleRange] match {
          case Right(value) => value == sr
          case Left(value) => false
        }
    })
  }

}
