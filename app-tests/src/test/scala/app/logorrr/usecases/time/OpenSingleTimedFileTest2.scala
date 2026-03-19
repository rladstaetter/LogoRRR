package app.logorrr.usecases.time


import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

class OpenSingleTimedFileTest2 extends SingleFileApplicationTest(TestFiles.timedLog):

  /**
   * this test sets the columns correctly but doesn't specify the format.
   * Expected behavior is that the timestamp settings is not specified (None).
   * */
  @Test def setPositionCorrectlyAndClickOnSetFormatTest(): Unit =
    setCorrectStartAndEndColumns(fileId)
    clickOnSetFormatButton(fileId)

    // settings aren't set after click on set button with invalid settings
    assert(!LogoRRRGlobals.getLogFileSettings(fileId).mutTimeSettings.validBinding.get())
