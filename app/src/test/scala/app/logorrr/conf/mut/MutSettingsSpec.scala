package app.logorrr.conf.mut

import app.logorrr.conf.{Settings, StageSettings}
import org.scalacheck.Prop
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.Checkers


class MutSettingsSpec extends AnyWordSpec with Checkers {

  "MutSettings" should {
    "dese" in {
      val s = Settings(StageSettings(0.15142984837327833,0.5216122226307276,1,1),Map(),None)
      assert(s == MutSettings(s).petrify())
    }
    "de/serialize" in {
      check(Prop.forAll(SettingsSpec.gen) {
        expected: Settings => expected == MutSettings(MutSettings(expected).petrify()).petrify()
      })
    }
  }

}
