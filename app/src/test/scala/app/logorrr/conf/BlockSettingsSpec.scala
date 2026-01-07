package app.logorrr.conf

import org.scalacheck.Gen

object BlockSettingsSpec {

  val gen: Gen[BlockSettings] = Gen.posNum[Int].map(BlockSettings.apply)

}
