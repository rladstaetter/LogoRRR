package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.*
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.usecases.time.SomeSettingTest
import app.logorrr.views.ops.time.{SliderVBox, TimerSlider}
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.{Disabled, Test}



/**
 * Check if LogoRRR starts with certain settings it displays
 *
 * - two sliders
 * - the positions are set correctly
 * - labels display the correct information
 *
 */
class Issue261RememberSettingsTest extends SomeSettingTest:

  override protected lazy val settings: Settings = Settings(
    StageSettings(TestSettings.stageArea)
    , Map(TestFiles.timedLog.value ->
      LogFileSettings.mk(TestFiles.timedLog, TestSettings.DefaultGroups.searchTermGroups.tail.head)
        .copy(blockSize = 50
          , someTimeSettings = Option(TimeSettings(0, 23, "yyyy-MM-dd HH:mm:ss,SSS"))))
    , None
    , None
    , TestSettings.Groups
    , None
  )


  @Test def checkVisibilityOfSlidersAndLabelText(): Unit =
    val logFileSettings: MutLogFileSettings = LogoRRRGlobals.getLogFileSettings(fileId)
    assert(logFileSettings.mutTimeSettings.validBinding.get())

    // check existence of sliders
    waitForVisibility(SliderVBox.uiNode(fileId, Pos.CENTER_LEFT))
    waitForVisibility(SliderVBox.uiNode(fileId, Pos.CENTER_RIGHT))

    performActions()




