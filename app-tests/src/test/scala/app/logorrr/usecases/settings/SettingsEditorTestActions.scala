package app.logorrr.usecases.settings

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.uinodes.{LogoRRRMenu, SettingsEditor}

trait SettingsEditorTestActions:
  self: TestFxBaseApplicationTest =>

  def withOpenedSettingsEditor(f: => Unit): Unit =
    waitAndClickVisibleItem(LogoRRRMenu.Self)
    waitAndClickVisibleItem(LogoRRRMenu.Settings)
    f
    waitAndClickVisibleItem(SettingsEditor.CloseButton)
