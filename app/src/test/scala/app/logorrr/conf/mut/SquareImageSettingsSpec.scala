package app.logorrr.conf.mut

import app.logorrr.conf.SquareImageSettings
import org.scalacheck.Gen

object SquareImageSettingsSpec {

  val gen: Gen[SquareImageSettings] = for {
    width <- Gen.posNum[Int]
  } yield SquareImageSettings(width)
}
