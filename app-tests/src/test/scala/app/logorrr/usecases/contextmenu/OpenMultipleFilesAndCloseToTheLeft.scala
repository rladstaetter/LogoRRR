package app.logorrr.usecases.contextmenu

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.actions.CloseLeftFilesMenuItem
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

class OpenMultipleFilesAndCloseToTheLeft
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions {

  @Test def openFilesAndActivateLastAndCloseAllToTheLeft(): Unit = {
    TestFiles.seq.foreach {
      p => openFile(p)
    }
    checkForNonEmptyTabPane()
    val looksi = lookup(UiNodes.LogFileHeaderTabs)
    // activate first tab
    val lastNodeQuery = looksi.nth(TestFiles.seq.size - 1)
    clickOn(lastNodeQuery.query[StackPane]())
    clickOn(lastNodeQuery.query[StackPane](), MouseButton.SECONDARY)
    waitAndClickVisibleItem(CloseLeftFilesMenuItem.uiNode(TestFiles.seq.last))

    expectCountOfOpenFiles(1)

  }

}
