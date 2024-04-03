package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.LogoRRRNodes
import org.testfx.api.FxRobotInterface

/**
 * Mix in if you want to be able to close all files in your test
 */
trait CanCloseAllFiles {
  self: TestFxBaseApplicationTest =>

  protected def closeAllFiles(): FxRobotInterface = {
    clickOnNode(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuCloseAll)
    clickOnNode(LogoRRRNodes.FileMenuCloseAll)
  }

}
