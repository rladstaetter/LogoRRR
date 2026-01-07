package app.logorrr.usecases.textview

import app.logorrr.TestFiles
import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.logfiletab.{TextConstants, TextSizeSlider}
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Test

/**
 * * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class SimpleTextSizeTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  @Test def testTextSizeChange(): Unit = {
    openFile(fileId)
    waitForVisibility(IncreaseTextSizeButton.uiNode(fileId))
    waitForVisibility(DecreaseTextSizeButton.uiNode(fileId))

    val size = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    val count = 10

    for (_ <- 1 to count) increaseTextSize(fileId)
    assert(size + (TextConstants.fontSizeStep * count) == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)

    // decrease again
    for (_ <- 1 to 10) decreaseTextSize(fileId)
    assert(size == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)
  }

  @Test def testIncreaseTextSizeWithSlider() : Unit = {
    openFile(fileId)
    waitForVisibility(TextSizeSlider.uiNode(fileId))

    val slider0 = drag(TextSizeSlider.uiNode(fileId).ref).moveBy(0,0)
    val size = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    val movedSlider = slider0.moveBy(200, 0)
    assert(size < LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)
    movedSlider.moveBy(-200, 0).release(MouseButton.PRIMARY)
    assert(LogoRRRGlobals.getLogFileSettings(fileId).getFontSize == size)

  }

  private def increaseTextSize(fileId: FileId): Unit = {
    clickOn(IncreaseTextSizeButton.uiNode(fileId))
  }

  private def decreaseTextSize(fileId: FileId): Unit = {
    clickOn(DecreaseTextSizeButton.uiNode(fileId))
  }

}
