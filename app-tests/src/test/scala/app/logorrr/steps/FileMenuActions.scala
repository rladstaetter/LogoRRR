package app.logorrr.steps

import app.logorrr.io.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.UiNodes
import app.logorrr.views.logfiletab.LogFileTab
import org.testfx.api.FxRobotInterface

/**
 * Mix in if you need to be able to open a file in your test
 */
trait FileMenuActions extends VisibleItemActions {
  self: TestFxBaseApplicationTest =>

  protected def openFile(fileId: FileId): Unit = {
    waitAndClickVisibleItem(UiNodes.FileMenu.Self)
    waitAndClickVisibleItem(UiNodes.FileMenu.OpenFile)
    waitForVisibility(LogFileTab.uiNode(fileId))
  }

  protected def closeAllFiles(): FxRobotInterface = {
    clickOn(UiNodes.FileMenu.Self)
    waitForVisibility(UiNodes.FileMenu.CloseAll)
    clickOn(UiNodes.FileMenu.CloseAll)
  }


  def quitApplication(): Unit = {
    waitAndClickVisibleItem(UiNodes.FileMenu.Self)
    waitAndClickVisibleItem(UiNodes.FileMenu.CloseApplication)
  }

}

