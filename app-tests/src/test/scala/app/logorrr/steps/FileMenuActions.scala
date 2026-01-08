package app.logorrr.steps

import app.logorrr.conf.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.uinodes.FileMenu
import app.logorrr.views.logfiletab.LogFileTab
import org.testfx.api.FxRobotInterface

/**
 * Mix in if you need to be able to open a file in your test
 */
trait FileMenuActions extends VisibleItemActions:
  self: TestFxBaseApplicationTest =>

  protected def openFile(fileId: FileId): Unit =
    waitAndClickVisibleItem(FileMenu.Self)
    waitAndClickVisibleItem(FileMenu.OpenFile)
    waitForVisibility(LogFileTab.uiNode(fileId))

  protected def closeAllFiles(): FxRobotInterface =
    clickOn(FileMenu.Self)
    waitForVisibility(FileMenu.CloseAll)
    clickOn(FileMenu.CloseAll)


