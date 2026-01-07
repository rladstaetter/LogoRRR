package app.logorrr.conf

import org.scalacheck.Gen

object StageSettingsSpec {

  val gen: Gen[StageSettings] = for {
    x <- Gen.posNum[Double]
    y <- Gen.posNum[Double]
    width <- Gen.posNum[Int]
    height <- Gen.posNum[Int]
  } yield StageSettings(x, y, width, height)
}
