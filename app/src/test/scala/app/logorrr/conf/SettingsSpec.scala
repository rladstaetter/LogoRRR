package app.logorrr.conf

import org.scalacheck.Gen

object SettingsSpec {

  val gen: Gen[Settings] = for {
    stageSettings <- StageSettingsSpec.gen
  } yield Settings(stageSettings, Map(), None, None, Map())

}
