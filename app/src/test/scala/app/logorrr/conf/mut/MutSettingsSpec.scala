package app.logorrr.conf.mut

import app.logorrr.LogoRRRSpec
import app.logorrr.conf.{Settings, SettingsSpec, StageSettings}
import app.logorrr.util.PersistenceManager
import org.scalacheck.Prop


class MutSettingsSpec extends LogoRRRSpec:


  def mkMutSettings(manager: PersistenceManager, settings: Settings): MutSettings =
    val s = new MutSettings
    s.setStageSettings(settings.stageSettings)
    s.setLogFileSettings(manager, settings.fileSettings)
    s


  "MutSettings" should :
    "deserialize" in :
      val manager = new PersistenceManager
      val s = Settings(StageSettings(0.15142984837327833, 0.5216122226307276, 1, 1), Map(), None, None, Seq(), None)
      assert(s == mkMutSettings(manager, s).mkImmutable())
      manager.shutdown()
    "de/serialize" in :
      val m = new PersistenceManager
      check(Prop.forAll(SettingsSpec.gen) {
        (expected: Settings) =>
          expected == mkMutSettings(m, mkMutSettings(m, expected).mkImmutable()).mkImmutable()
      })
      m.shutdown()

