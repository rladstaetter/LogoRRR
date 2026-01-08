package app.logorrr.conf

import org.scalacheck.Gen

import scala.util.Random


object SimpleRangeSpec:

  val gen: Gen[SimpleRange] = for
    start <- Gen.posNum[Int]
    end <- Gen.chooseNum(start, Random.nextInt(start + 100) + start)
  yield SimpleRange(start, end)






