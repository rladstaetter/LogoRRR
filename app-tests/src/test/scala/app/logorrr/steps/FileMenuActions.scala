package app.logorrr.steps

import app.logorrr.io.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.UiNodes
import app.logorrr.views.logfiletab.LogFileTab
import org.testfx.api.FxRobotInterface

/**
 * Mix in if you need to be able to open a file in your test
 */
trait FileMenuActions extends VisibleItemActions {
  self: TestFxBaseApplicationTest =>

  protected def openFile(fileId: FileId): Unit = {
    waitAndClickVisibleItem(UiNodes.FileMenu)
    waitAndClickVisibleItem(UiNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.uiNode(fileId))
  }

  protected def closeAllFiles(): FxRobotInterface = {
    clickOn(UiNodes.FileMenu)
    waitForVisibility(UiNodes.FileMenuCloseAll)
    clickOn(UiNodes.FileMenuCloseAll)
  }


  def quitApplication(): Unit = {
    waitAndClickVisibleItem(UiNodes.FileMenu)
    waitAndClickVisibleItem(UiNodes.FileMenuCloseApplication)
  }

}

