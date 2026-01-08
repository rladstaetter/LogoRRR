package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.UiNode
import org.testfx.api.FxRobotInterface

trait VisibleItemActions:
  self: TestFxBaseApplicationTest =>

  def waitAndClickVisibleItem(node: UiNode): FxRobotInterface =
    waitForVisibility(node)
    clickOn(node)

