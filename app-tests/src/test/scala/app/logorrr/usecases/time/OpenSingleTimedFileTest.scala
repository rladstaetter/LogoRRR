package app.logorrr.usecases.time

import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.ops.time.{SliderVBox, TimestampSettingsButton}
import app.logorrr.views.settings.timestamp.{LogViewLabel, TimeFormatTextField, TimestampFormatResetButton, TimestampFormatSetButton}
import javafx.geometry.Pos
import org.junit.jupiter.api.Test

/**
 * Checks timestamp scenario:
 *
 * - open a log file with timestamp information, but no timestamp format set
 *
 * - clicks on timersettings button to open timer settings stage and clicks reset
 * - clicks on timersettings button and click on 'set format'
 * - clicks on timersettings button
 *
 * -
 */
class OpenSingleTimedFileTest extends SingleFileApplicationTest(TestFiles.timedLog) {

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openTimestampSettingsTest(): Unit = {
    openFile(fileId)
    clickOn(TimestampSettingsButton.uiNode(fileId))
    clickOn(TimestampFormatResetButton.uiNode(fileId))
    // settings aren't set after click on reset button
    assert(LogoRRRGlobals.getLogFileSettings(fileId).hasTimestampSetting.not.get())
  }

  // just click on the setFormat button, no position given
  @Test def setFormatTest(): Unit = {
    openFile(fileId)
    clickOn(TimestampSettingsButton.uiNode(fileId))
    clickOn(TimestampFormatSetButton.uiNode(fileId))
    // settings aren't set after click on reset button
    assert(LogoRRRGlobals.getLogFileSettings(fileId).hasTimestampSetting.get())

    // label is set to gibberish since we couldn't parse the timestamp properly (no position set)
    expectLabelText(fileId, Pos.CENTER_LEFT, "1970-01-01 01:00:00.000")
    expectLabelText(fileId, Pos.CENTER_RIGHT, "1970-01-01 01:00:00.000")
  }

  @Test def setPositionAndFormatTest(): Unit = {
    openFile(fileId)
    clickOn(TimestampSettingsButton.uiNode(fileId))

    // set position
    clickOn(LogViewLabel.uiNode(fileId, 1, 0))
    clickOn(LogViewLabel.uiNode(fileId, 1, 23))

    // set format for this log file (delete the default format)
    clickOn(TimeFormatTextField.uiNode(fileId)).eraseText(23).write("yyyy-MM-dd HH:mm:ss,SSS")

    // set format and close stage
    clickOn(TimestampFormatSetButton.uiNode(fileId))

    assert(LogoRRRGlobals.getLogFileSettings(fileId).hasTimestampSetting.get())

    // expect lowest and highest timestamp
    expectLabelText(fileId, Pos.CENTER_LEFT, "2023-08-02 21:16:33,193")
    expectLabelText(fileId, Pos.CENTER_RIGHT, "2023-08-02 21:16:38,656")

  }

  def expectLabelText(fileId: FileId, pos: Pos, expectedText: String): Unit = {
    waitForPredicate[SliderVBox](SliderVBox.uiNode(fileId, pos), classOf[SliderVBox], sliderBox => {
      sliderBox.label.getText == expectedText
    })
  }

}

