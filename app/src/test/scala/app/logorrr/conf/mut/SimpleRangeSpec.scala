package app.logorrr.conf.mut

import app.logorrr.views.settings.timestamp.SimpleRange
import org.scalacheck.Gen

import scala.util.Random


object SimpleRangeSpec {

  val gen: Gen[SimpleRange] = for {
    start <- Gen.posNum[Int]
    end <- Gen.chooseNum(start, Random.nextInt(start + 100) + start)
  } yield SimpleRange(start, end)
}






