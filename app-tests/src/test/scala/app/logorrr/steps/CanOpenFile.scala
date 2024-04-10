package app.logorrr.steps

import app.logorrr.io.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.UiNodes
import app.logorrr.views.logfiletab.LogFileTab

import java.nio.file.Path

/**
 * Mix in if you need to be able to open a file in your test
 */
trait CanOpenFile extends VisibleItemActions {
  self: TestFxBaseApplicationTest  =>

  protected def openFile(path: Path): Unit = {
    waitAndClickVisibleItem(UiNodes.FileMenu)
    waitAndClickVisibleItem(UiNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.uiNode(FileId(path)))
  }

}

