package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.uinodes.LogoRRRMenu

trait LogoRRRAppMenuActions extends VisibleItemActions:
  self: TestFxBaseApplicationTest =>

  def quitApplication(): Unit =
    waitAndClickVisibleItem(LogoRRRMenu.Self)
    waitAndClickVisibleItem(LogoRRRMenu.CloseApplication)
