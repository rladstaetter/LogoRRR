package app.logorrr.usecases.contextmenu

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.actions.CloseAllFilesMenuItem
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

import scala.util.Random

class OpenMultipleFilesAndCloseAllViaContextMenu
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions:

  @Test def openFilesAndCloseAllViaContextMenuItem(): Unit =
    files.foreach(openFile)

    expectCountOfOpenFiles(TestFiles.seq.size)

    val selectedFile = Random.nextInt(TestFiles.seq.size)

    val looksi = lookup(UiNodes.LogFileHeaderTabs)
    // activate random tab
    val lastNodeQuery = looksi.nth(selectedFile)
    clickOn(lastNodeQuery.query[StackPane]())
    clickOn(lastNodeQuery.query[StackPane](), MouseButton.SECONDARY)
    waitAndClickVisibleItem(CloseAllFilesMenuItem.uiNode(TestFiles.seq(selectedFile)))

    expectCountOfOpenFiles(0)





