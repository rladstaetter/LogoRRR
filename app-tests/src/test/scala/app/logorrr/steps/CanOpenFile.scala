package app.logorrr.steps

import app.logorrr.io.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.UiNodes
import app.logorrr.views.logfiletab.LogFileTab

import java.nio.file.Path

/**
 * Mix in if you need to be able to open a file in your test
 */
trait CanOpenFile {
  self: TestFxBaseApplicationTest =>

  protected def openFile(path: Path): Unit = {
    waitForVisibility(UiNodes.FileMenu)
    clickOn(UiNodes.FileMenu)
    waitForVisibility(UiNodes.FileMenuOpenFile)
    clickOn(UiNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.uiNode(FileId(path)))
  }

}
