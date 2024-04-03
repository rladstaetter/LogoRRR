package app.logorrr.usecases.openclose

import app.logorrr.io.FileId
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.{MultipleFileApplicationTest, TestFiles}
import javafx.scene.control.TabPane
import org.junit.jupiter.api.Test

/**
 * Check if multiple files can be opened and then closed again via file menu 'close all'
 */
class OpenAndCloseMultipleFilesViaMenuTest extends MultipleFileApplicationTest(TestFiles.seq) {

  @Test def openFilesAndCloseAllViaMenu(): Unit = {
    TestFiles.seq.foreach {
      p => openFile(FileId(p))
    }
    // now close them all again
    clickOnNode(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuCloseAll)

    clickOnNode(LogoRRRNodes.FileMenuCloseAll)

    waitForPredicate[TabPane](LogoRRRNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }

  private def openFile(fileId: FileId): Unit = {
    waitForVisibility(LogoRRRNodes.FileMenu)
    clickOnNode(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOnNode(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(fileId))
  }
}
