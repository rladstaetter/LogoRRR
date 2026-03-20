package app.logorrr.usecases.blockview

import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.logfiletab.BlockSizeSlider
import javafx.scene.control.Slider
import org.junit.jupiter.api.Test

import java.util.concurrent.Callable

/**
 * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class BlockSizeWithSliderTest extends SingleFileApplicationTest(TestFiles.simpleLog0):

  @Test def testSliderSideEffectToBlockSizeSetting(): Unit =
    openFile(fileId)

    val slider = lookup[Slider](BlockSizeSlider.uiNode(fileId))

    val sizeBefore = LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize

    // 1. Move the slider programmatically to simulate a drag
    interact(new Callable[Unit] {
      override def call(): Unit = slider.setValue(slider.getValue + 20)
    })

    // 2. Verify the model updated
    val sizeAfterMove = LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize
    assert(sizeBefore < sizeAfterMove, s"Size should have increased, but was $sizeAfterMove")

    // 3. Reset the value
    interact(new Callable[Unit] {
      override def call(): Unit = slider.setValue(slider.getValue - 20)
    })

    assert(LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize == sizeBefore)