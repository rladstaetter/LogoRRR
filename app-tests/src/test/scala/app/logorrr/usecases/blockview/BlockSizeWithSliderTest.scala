package app.logorrr.usecases.blockview

import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.logfiletab.BlockSizeSlider
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Test

/**
 * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class BlockSizeWithSliderTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  @Test def search(): Unit = {
    openFile(fileId)
    waitForVisibility(BlockSizeSlider.uiNode(fileId))

    val slider0 = drag(BlockSizeSlider.uiNode(fileId).ref).moveBy(0,0)
    val size = LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize
    val movedSlider = slider0.moveBy(200, 0)
    assert(size < LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize)
    movedSlider.moveBy(-200, 0).release(MouseButton.PRIMARY)
    assert(LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize == size)
  }

}
