package app.logorrr.conf.mut

import app.logorrr.conf.Settings
import org.scalacheck.Prop
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.Checkers


class MutSettingsSpec extends AnyWordSpec with Checkers {

  "MutSettings" should {
    "de/serialize" in {
      check(Prop.forAll(SettingsSpec.gen) {
        expected: Settings => expected == MutSettings(MutSettings(expected).petrify()).petrify()
      })
    }
  }

}
