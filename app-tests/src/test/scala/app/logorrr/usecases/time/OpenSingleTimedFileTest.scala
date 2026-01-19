package app.logorrr.usecases.time

import app.logorrr.TestFiles
import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.ops.time.{SliderVBox, TimerSlider, TimestampSettingsButton}
import app.logorrr.views.settings.timestamp.{LogViewLabel, TimeFormatTextField, TimestampFormatResetButton, TimestampFormatSetButton}
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Test

/**
 * Checks timestamp scenario:
 *
 * - open a log file with timestamp information, but no timestamp format set
 *
 * - clicks on timersettings button to open timer settings stage and clicks reset
 * - clicks on timersettings button and click on 'set format'
 * - clicks on timersettings button and configure the position of the timestamp and set the timestamp format
 *
 */
class OpenSingleTimedFileTest extends SingleFileApplicationTest(TestFiles.timedLog):

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openTimestampSettingsTest(): Unit =
    openFile(fileId)
    clickOn(TimestampSettingsButton.uiNode(fileId))
    clickOn(TimestampFormatResetButton.uiNode(fileId))
    // settings aren't set after click on reset button
    assert(LogoRRRGlobals.getLogFileSettings(fileId).hasTimestampSetting.not.get())

  // just click on the setFormat button, no position given
  @Test def setFormatTest(): Unit =
    openFile(fileId)
    clickOn(TimestampSettingsButton.uiNode(fileId))
    clickOn(TimestampFormatSetButton.uiNode(fileId))

    // settings aren't set after click on set button with invalid settings
    assert(!LogoRRRGlobals.getLogFileSettings(fileId).hasTimestampSetting.get())

  @Test def setPositionAndFormatTest(): Unit =
    openFile(fileId)
    waitAndClickVisibleItem(TimestampSettingsButton.uiNode(fileId))


    // set position twice (?!)
    waitAndClickVisibleItem(LogViewLabel.uiNode(fileId, 1, 0))
    clickOn(LogViewLabel.uiNode(fileId, 1, 0))
    clickOn(LogViewLabel.uiNode(fileId, 1, 23))

    // set format for this log file (delete the default format)
    waitAndClickVisibleItem(TimeFormatTextField.uiNode(fileId)).eraseText(4).write(",SSS")

    // set format and close stage
    waitAndClickVisibleItem(TimestampFormatSetButton.uiNode(fileId))

    assert(LogoRRRGlobals.getLogFileSettings(fileId).hasTimestampSetting.get())

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


  def expectLabelText(fileId: FileId, pos: Pos, expectedText: String): Unit =
    waitForPredicate[SliderVBox](SliderVBox.uiNode(fileId, pos), classOf[SliderVBox], sliderBox => {
      sliderBox.label.getText == expectedText
    })


