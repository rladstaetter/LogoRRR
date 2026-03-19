package app.logorrr.usecases.textview

import app.logorrr.TestFiles
import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.logfiletab.{TextConstants, TextSizeSlider}
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.scene.control.Slider
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Test

import java.util.concurrent.Callable
import scala.util.Random

/**
 * * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class SimpleTextSizeTest extends SingleFileApplicationTest(TestFiles.simpleLog0):

  private def increaseTextSize(fileId: FileId): Unit = clickOn(IncreaseTextSizeButton.uiNode(fileId))

  private def decreaseTextSize(fileId: FileId): Unit = clickOn(DecreaseTextSizeButton.uiNode(fileId))

  @Test def testTextSizeChange(): Unit =
    openFile(fileId)
    waitForVisibility(IncreaseTextSizeButton.uiNode(fileId))
    waitForVisibility(DecreaseTextSizeButton.uiNode(fileId))

    val size = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    val count = 10

    for _ <- 1 to count do increaseTextSize(fileId)
    assert(size + (TextConstants.fontSizeStep * count) == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)

    // decrease again
    for _ <- 1 to 10 do decreaseTextSize(fileId)
    assert(size == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)

  @Test def testIncreaseTextSizeWithSlider(): Unit =
    openFile(fileId)
    val slider = lookup[Slider](TextSizeSlider.uiNode(fileId))
    val initialSize = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    val targetValue = slider.getValue + Random.nextInt(50)

    // 1. Update the slider value directly on the FX Thread
    interacT(slider.setValue(targetValue))

    // 2. Wait for the LogoRRRGlobals to reflect the change.
    // We poll for up to 2 seconds to account for async listeners or CI lag.
    org.testfx.util.WaitForAsyncUtils.waitFor(2, java.util.concurrent.TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize > initialSize
    })

    val increasedSize = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    assert(increasedSize > initialSize, s"Expected font size to increase, but stayed at $initialSize")

    // 3. Reset to initial value
    interacT(slider.setValue(initialSize))

    // 4. Wait for it to return
    org.testfx.util.WaitForAsyncUtils.waitFor(2, java.util.concurrent.TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = {
        LogoRRRGlobals.getLogFileSettings(fileId).getFontSize == initialSize
      }
    })

