package app.logorrr.usecases.contextmenu

import app.logorrr.TestFiles
import app.logorrr.steps.{CheckTabPaneActions, VisibleItemActions}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.actions.OpenInFinderMenuItem
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

/**
 * Checks if LogoRRR can open a file in the file explorer
 */
class OpenSingleFileAndOpenInFinderTest
  extends SingleFileApplicationTest(TestFiles.simpleLog0)
    with VisibleItemActions
    with CheckTabPaneActions:

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openSingleFileAndOpenInFinderTest(): Unit =
    // basically skips this until https://github.com/rladstaetter/LogoRRR/issues/221#issuecomment-2083771649
    if false then
      checkForEmptyTabPane()
      openFile(fileId)
      checkForNonEmptyTabPane()
      clickOn(lookup(UiNodes.LogFileHeaderTabs).query[StackPane](), MouseButton.SECONDARY)
      waitAndClickVisibleItem(OpenInFinderMenuItem.uiNode(fileId))
      checkForNonEmptyTabPane()


