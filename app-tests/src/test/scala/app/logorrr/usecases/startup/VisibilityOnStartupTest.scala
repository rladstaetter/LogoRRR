package app.logorrr.usecases.startup

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.autoscroll.AutoScrollCheckBox
import app.logorrr.views.logfiletab.LogoRRRChunkListView
import app.logorrr.views.ops.{ClearLogButton, CopyLogButton, DecreaseBlockSizeButton, IncreaseBlockSizeButton}
import app.logorrr.views.search.{SearchButton, SearchTextField}
import app.logorrr.views.text.LogTextView
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import org.junit.jupiter.api.Test
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

/**
 * Tests if all major UI elements are visible with default settings on first startup
 */
class VisibilityOnStartupTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  @Test def checkIfAllMajorUIElementsAreVisibleWithDefaultSettings(): Unit = {
    openFile(fileId)
    //wait until UI is ready

    val elements = Seq(
       SearchTextField.uiNode(fileId)
      , SearchButton.uiNode(fileId)
      , DecreaseBlockSizeButton.uiNode(fileId)
      , IncreaseBlockSizeButton.uiNode(fileId)
      , DecreaseTextSizeButton.uiNode(fileId)
      , IncreaseTextSizeButton.uiNode(fileId)
      , AutoScrollCheckBox.uiNode(fileId)
      , ClearLogButton.uiNode(fileId)
      , CopyLogButton.uiNode(fileId)
      , LogoRRRChunkListView.uiNode(fileId)
      , LogTextView.uiNode(fileId)
    )

    // check all elements
    for (uiElement <- elements) {
      waitForVisibility(uiElement)
      FxAssert.verifyThat(lookup(uiElement.ref), NodeMatchers.isVisible)
    }
  }


}


