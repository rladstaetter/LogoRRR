package app.logorrr

import app.logorrr.io.FileId
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab
import org.junit.jupiter.api.Test

import java.nio.file.Paths



class OpenFileTest extends LogoRRRSingleFileApplicationTest(Paths.get("src/test/resources/app/logorrr/OpenFileTest.log")) {

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openFileTest(): Unit = {
    waitForVisibility(LogoRRRNodes.FileMenu)
    clickOn(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOn(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(FileId(path)))
  }

}


