package app.logorrr.usecases.chunkview

import app.logorrr.TestFiles
import app.logorrr.conf.{LogoRRRGlobals, Settings, StageSettings}
import app.logorrr.io.FileId
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenSingleFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.ops.{DecreaseBlockSizeButton, IncreaseBlockSizeButton}
import app.logorrr.views.search.OpsToolBar
import org.junit.jupiter.api.Test

/**
 * Test if multiple symmetric applications of increase and decrease actions lead to the same result again
 */
class SimpleBlockSizeTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  override val services: LogoRRRServices = LogoRRRServices(Settings.Default.copy(stageSettings = StageSettings(100, 100, 1200, 600))
    , new MockHostServices
    , new OpenSingleFileService(Option(path))
    , isUnderTest = true)

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
