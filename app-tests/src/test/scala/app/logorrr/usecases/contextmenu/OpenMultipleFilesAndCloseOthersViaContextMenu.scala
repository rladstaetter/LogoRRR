package app.logorrr.usecases.contextmenu

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.actions.CloseOtherFilesMenuItem
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

import scala.util.Random

class OpenMultipleFilesAndCloseOthersViaContextMenu
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions {

  @Test def openFilesAndCloseAllViaContextMenuItem(): Unit = {
    TestFiles.seq.foreach {
      p => openFile(p)
    }
    expectCountOfOpenFiles(TestFiles.seq.size)
    val selectedFile = Random.nextInt(TestFiles.seq.size)

    val looksi = lookup(UiNodes.LogFileHeaderTabs)
    // activate random tab
    val lastNodeQuery = looksi.nth(selectedFile)
    clickOn(lastNodeQuery.query[StackPane]())
    clickOn(lastNodeQuery.query[StackPane](), MouseButton.SECONDARY)
    waitAndClickVisibleItem(CloseOtherFilesMenuItem.uiNode(TestFiles.seq(selectedFile)))

    expectCountOfOpenFiles(1)

  }

}
