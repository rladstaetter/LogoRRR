package app.logorrr.usecases.textview

import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import org.junit.jupiter.api.Test

/**
 * * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class SimpleTextSizeTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  @Test def search(): Unit = {
    openFile(fileId)

    val size = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    val count = 10

    for (_ <- 1 to count) increaseTextSize(fileId)
    assert(size + (OpsToolBar.fontSizeStep * count) == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)

    // decrease again
    for (_ <- 1 to 10) decreaseTextSize(fileId)
    assert(size == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)
  }

  private def increaseTextSize(fileId: FileId): Unit = {
    waitForVisibility(IncreaseTextSizeButton.uiNode(fileId))
    clickOn(IncreaseTextSizeButton.uiNode(fileId))
  }

  private def decreaseTextSize(fileId: FileId): Unit = {
    waitForVisibility(DecreaseTextSizeButton.uiNode(fileId))
    clickOn(DecreaseTextSizeButton.uiNode(fileId))
  }

}
