package app.logorrr.usecases.time

import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.settings.timestamp.TimestampFormatResetButton
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
class OpenSingleTimedFileTest1 extends SingleFileApplicationTest(TestFiles.timedLog):

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openTimestampSettingsTest(): Unit =
    openFileAndTimestampDialogue(fileId)

    clickOn(TimestampFormatResetButton.uiNode(fileId))
    // settings aren't set after click on reset button
    assert(LogoRRRGlobals.getLogFileSettings(fileId).mutTimeSettings.validBinding.not.get())
