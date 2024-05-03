package app.logorrr.usecases.contextmenu

import app.logorrr.TestFiles
import app.logorrr.io.FileId
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.UiNodes
import app.logorrr.views.logfiletab.actions.CloseRightFilesMenuItem
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

class OpenMultipleFilesAndCloseToTheRight
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions {

  @Test def openFilesAndActivateFirstAndCloseAllToTheRight(): Unit = {
    TestFiles.seq.foreach {
      p => openFile(p)
    }
    checkForNonEmptyTabPane()
    // activate first tab
    clickOn(lookup(UiNodes.LogFileHeaderTabs).query[StackPane]())
    clickOn(lookup(UiNodes.LogFileHeaderTabs).query[StackPane](), MouseButton.SECONDARY)
    waitAndClickVisibleItem(CloseRightFilesMenuItem.uiNode(FileId(TestFiles.seq.head)))

    expectCountOfOpenFiles(1)

  }

}
