package app.logorrr.usecases.time

import app.logorrr.conf.{LogoRRRGlobals, TimeSettings}
import app.logorrr.views.settings.timestamp.{TimeFormatTextField, TimestampFormatSetButton}
import org.junit.jupiter.api.Test

class OpenSingleTimedFileTest4 extends SomeSettingTest:

  @Test def setPositionAndFormatTest(): Unit =
    setCorrectStartAndEndColumns(fileId)

    // 1. Set format for this log file
    waitAndClickVisibleItem(TimeFormatTextField.uiNode(fileId))
      .write(TimeSettings.DefaultPattern)
      .eraseText(4)
      .write(",SSS")

    // 2. Commit format and close stage
    waitAndClickVisibleItem(TimestampFormatSetButton.uiNode(fileId))

    assert(LogoRRRGlobals.getLogFileSettings(fileId).mutTimeSettings.validBinding.get())

    performActions()
