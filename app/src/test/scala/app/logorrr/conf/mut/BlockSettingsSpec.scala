package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import org.scalacheck.Gen

object BlockSettingsSpec {

  val gen: Gen[BlockSettings] = Gen.posNum[Int].map(BlockSettings.apply)

}
