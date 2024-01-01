package app.logorrr.conf.mut

import app.logorrr.LogoRRRSpec
import app.logorrr.conf.{Settings, StageSettings}
import org.scalacheck.Prop


class MutSettingsSpec extends LogoRRRSpec {

  def mkMutSettings(settings: Settings): MutSettings = {
    val s = new MutSettings
    s.setStageSettings(settings.stageSettings)
    s.setLogFileSettings(settings.fileSettings)
    s.setSomeActive(settings.someActive)
    s
  }
  "MutSettings" should {
    "deserialize" in {
      val s = Settings(StageSettings(0.15142984837327833, 0.5216122226307276, 1, 1), Map(), None, None)
      assert(s == mkMutSettings(s).petrify())
    }
    "de/serialize" in {
      check(Prop.forAll(SettingsSpec.gen) {
        expected: Settings => expected == mkMutSettings(mkMutSettings(expected).petrify()).petrify()
      })
    }
  }

}
