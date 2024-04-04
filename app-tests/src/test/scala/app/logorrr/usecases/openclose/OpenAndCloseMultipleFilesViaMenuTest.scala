package app.logorrr.usecases.openclose

import app.logorrr.views.UiNodes
import app.logorrr.TestFiles
import app.logorrr.usecases.MultipleFileApplicationTest
import javafx.scene.control.TabPane
import org.junit.jupiter.api.Test


/**
 * Check if multiple files can be opened and then closed again via file menu 'close all'
 */
class OpenAndCloseMultipleFilesViaMenuTest extends MultipleFileApplicationTest(TestFiles.seq){

  @Test def openFilesAndCloseAllViaMenu(): Unit = {
    TestFiles.seq.foreach {
      p => openFile(p)
    }
    // now close them all again
    clickOn(UiNodes.FileMenu)
    waitForVisibility(UiNodes.FileMenuCloseAll)

    clickOn(UiNodes.FileMenuCloseAll)

    waitForPredicate[TabPane](UiNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }

}
