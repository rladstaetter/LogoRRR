package app.logorrr.usecases.openclose

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.a11y.uinodes.FileMenu
import org.junit.jupiter.api.Test

/**
 * Check if multiple files can be opened and then closed again via file menu 'close all'
 */
class OpenAndCloseMultipleFilesViaMenuTest
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions:

  @Test def openFilesAndCloseAllViaMenu(): Unit =
    files.foreach(openFile)

    // now close them all again
    clickOn(FileMenu.Self)
    waitAndClickVisibleItem(FileMenu.CloseAll)

    checkForEmptyTabPane()


