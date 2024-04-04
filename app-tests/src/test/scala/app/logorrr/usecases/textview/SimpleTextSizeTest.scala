package app.logorrr.usecases.textview

import app.logorrr.TestFiles
import app.logorrr.conf.{LogoRRRGlobals, Settings, StageSettings}
import app.logorrr.io.FileId
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenSingleFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import org.junit.jupiter.api.Test

class SimpleTextSizeTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  override val services = LogoRRRServices(Settings.Default.copy(stageSettings = StageSettings(100, 100, 1200, 600))
    , new MockHostServices
    , new OpenSingleFileService(Option(path))
    , isUnderTest = true)

  @Test def search(): Unit = {
    openFile(path)
    val fileId = FileId(path)

    val size = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize
    val count = 10

    for (_ <- 1 to count) increaseTextSize(fileId)
    assert(size + count == LogoRRRGlobals.getLogFileSettings(fileId).getFontSize)

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
