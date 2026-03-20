package app.logorrr.usecases.time


import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

class OpenSingleTimedFileTest3 extends SingleFileApplicationTest(TestFiles.timedLog):


  // just click on the setFormat button, no position given
  // this test shows that "nothing happens" if user doesn't choose region or text
  @Test def justClickOnSetFormatTest(): Unit =
    openFileAndTimestampDialogue(fileId)
    clickOnSetFormatButton(fileId)

    // settings aren't set after click on set button with invalid settings
    assert(!LogoRRRGlobals.getLogFileSettings(fileId).mutTimeSettings.validBinding.get())
