package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.UiNode
import org.testfx.api.FxRobotInterface

trait VisibleItemActions {
  self: TestFxBaseApplicationTest =>

  def waitAndClickVisibleItem(menu: UiNode): FxRobotInterface = {
    waitForVisibility(menu)
    clickOn(menu)
  }

}
