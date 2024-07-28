package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.Settings.calcDefaultScreenPosition
import app.logorrr.conf.{BlockSettings, Settings, StageSettings}
import app.logorrr.model.LogFileSettings
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

/**
 * https://github.com/rladstaetter/LogoRRR/issues/231
 *
 * Backing array for blockview / LPixelbuffer was not allocated/created correctly. For the end user, this
 * had severe impacts, like blocks weren't drawn correctly.
 *
 * */
class Issue231VisualGlitchInBoxView
  extends SingleFileApplicationTest(TestFiles.simpleLog1) {

  /** setup settings such that the issue is triggered and can be inspected visually  */
  override lazy val settings: Settings = Settings(
    StageSettings(calcDefaultScreenPosition())
    , Map(TestFiles.simpleLog1.value ->
      LogFileSettings(TestFiles.simpleLog1)
        .copy(
          blockSettings = BlockSettings(50)
          , dividerPosition = 0.599))
    , None
    , None
  )

  // atm this is only a setup test which helps to get LogoRRR in a repeatable, defined state
  // start LogoRRRApp afterwards to tinker around
  @Test def testIssue231(): Unit = {
    openFile(TestFiles.simpleLog1)

    // TODO add a test condition
    // it should test:
    // - mouse click outside the Chunks
    // - mouse click inside the Chunks
    // - mouse click on various boxes and check if the relevant text view is selected
    // - mouse click on various text view entries and see if the relevant boxes contain the correct color / highlight
    // - check the number of the displayed boxes (maybe just an image compare?)

  }

}
