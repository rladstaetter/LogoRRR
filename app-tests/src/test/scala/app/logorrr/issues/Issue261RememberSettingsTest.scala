package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.Settings.calcDefaultScreenPosition
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{BlockSettings, LogoRRRGlobals, Settings, StageSettings}
import app.logorrr.io.FileId
import app.logorrr.model.{LogFileSettings, TimestampSettings}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.ops.time.{SliderVBox, TimerSlider}
import app.logorrr.views.settings.timestamp.SimpleRange
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Test

/**
 * Check if LogoRRR starts with certain settings it displays
 *
 * - two sliders
 * - the positions are set correctly
 * - labels display the correct information
 *
 * See also OpenSingleF´TimedFileTest
 */
class Issue261RememberSettingsTest extends SingleFileApplicationTest(TestFiles.timedLog) {

  override protected lazy val settings: Settings = Settings(
    StageSettings(calcDefaultScreenPosition())
    , Map(TestFiles.timedLog.value ->
      LogFileSettings(TestFiles.timedLog)
        .copy(blockSettings = BlockSettings(50)
          , someTimestampSettings = Option(TimestampSettings(SimpleRange(0, 23), "yyyy-MM-dd HH:mm:ss,SSS"))))
    , None
    , None
  )

  @Test def checkVisibilityOfSlidersAndLabelText(): Unit = {
    val logFileSettings: MutLogFileSettings = LogoRRRGlobals.getLogFileSettings(fileId)
    assert(logFileSettings.hasTimestampSetting.get())

    // check existence of sliders
    waitForVisibility(SliderVBox.uiNode(fileId, Pos.CENTER_LEFT))
    waitForVisibility(SliderVBox.uiNode(fileId, Pos.CENTER_RIGHT))

    val earilestTimestamp = "2023-08-02 21:16:33,193"
    val latestTimestamp = "2023-08-02 21:16:38,656"

    // expect lowest and highest timestamp
    expectLabelText(fileId, Pos.CENTER_LEFT, earilestTimestamp)
    expectLabelText(fileId, Pos.CENTER_RIGHT, latestTimestamp)

    // now, change the right slider (starting with the middle of the slider)
    drag(SliderVBox.uiNode(fileId, Pos.CENTER_RIGHT).ref).moveBy(-TimerSlider.Width / 2, 0).release(MouseButton.PRIMARY)
    expectLabelText(fileId, Pos.CENTER_RIGHT, earilestTimestamp)

    // put right slider back, also move lower slider
    drag(SliderVBox.uiNode(fileId, Pos.CENTER_RIGHT).ref).moveBy(TimerSlider.Width, 0).release(MouseButton.PRIMARY)
    expectLabelText(fileId, Pos.CENTER_RIGHT, latestTimestamp)

    // drag lower slider to highest point
    drag(SliderVBox.uiNode(fileId, Pos.CENTER_LEFT).ref).moveBy(TimerSlider.Width / 2, 0).release(MouseButton.PRIMARY)
    expectLabelText(fileId, Pos.CENTER_LEFT, latestTimestamp)

  }

  def expectLabelText(fileId: FileId, pos: Pos, expectedText: String): Unit = {
    waitForPredicate[SliderVBox](SliderVBox.uiNode(fileId, pos), classOf[SliderVBox], sliderBox => {
      sliderBox.label.getText == expectedText
    })
  }

}

