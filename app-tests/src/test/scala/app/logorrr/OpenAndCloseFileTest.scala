package app.logorrr

import app.logorrr.io.FileId
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.control.TabPane
import org.junit.jupiter.api.Test

/**
 * Checks if a file can be opened and closed
 */
class OpenAndCloseFileTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openAndCloseFileTest(): Unit = {
    // wait until file menu is visible
    waitForVisibility(LogoRRRNodes.FileMenu)
    clickOn(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOn(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(FileId(path)))

    // file menu is already visible, we don't need to wait again
    // click on file menu and then close all button
    clickOn(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuCloseAll)
    clickOn(LogoRRRNodes.FileMenuCloseAll)

    waitForPredicate[TabPane](LogoRRRNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }

}
