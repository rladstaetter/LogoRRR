package app.logorrr.usecases.blockview

import app.logorrr.TestFiles
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.ops.{DecreaseBlockSizeButton, IncreaseBlockSizeButton}
import app.logorrr.views.search.OpsToolBar
import org.junit.jupiter.api.Test

/**
 * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class SimpleBlockSizeTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  @Test def search(): Unit = {
    openFile(path)
    val fileId = FileId(path)

    val size = LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize
    val count = 10

    for (_ <- 1 to count) increaseBlockSize(fileId)
    assert(size + (OpsToolBar.blockSizeStep * count) == LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize)

    // decrease again
    for (_ <- 1 to 10) decreaseBlockSize(fileId)
    assert(size == LogoRRRGlobals.getLogFileSettings(fileId).getBlockSize)
  }


  private def increaseBlockSize(fileId: FileId): Unit = {
    waitForVisibility(IncreaseBlockSizeButton.uiNode(fileId))
    clickOn(IncreaseBlockSizeButton.uiNode(fileId))
  }

  private def decreaseBlockSize(fileId: FileId): Unit = {
    waitForVisibility(DecreaseBlockSizeButton.uiNode(fileId))
    clickOn(DecreaseBlockSizeButton.uiNode(fileId))
  }

}
