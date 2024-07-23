package app.logorrr.usecases.openclose

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

/**
 * Checks if a file can be opened and closed
 */
class OpenAndCloseFileTestViaFileMenuCloseAllTest extends SingleFileApplicationTest(TestFiles.simpleLog0)
  with CheckTabPaneActions {

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openAndCloseFileTest(): Unit = {
    // wait until file menu is visible
    openFile(fileId)

    // file menu is already visible, we don't need to wait again
    // click on file menu and then close all button
    closeAllFiles()

    checkForEmptyTabPane()
  }

}
