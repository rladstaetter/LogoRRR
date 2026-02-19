package app.logorrr.usecases.settings

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.uinodes.{LogoRRRMenu, SettingsEditor}
import app.logorrr.views.settings.SettingsStgListView

trait SettingsEditorTestActions:
  self: TestFxBaseApplicationTest =>

  def withOpenedSettingsEditor(f: => Unit): Unit =
    waitAndClickVisibleItem(LogoRRRMenu.Self)
    waitAndClickVisibleItem(LogoRRRMenu.Settings)
    f
    waitAndClickVisibleItem(SettingsEditor.CloseButton)

  def lookupListView(): SettingsStgListView = lookup[SettingsStgListView](SettingsEditor.SettingsStgListView)

  protected def openSettingsEditorAndPerform(fn: SettingsStgListView => Unit): Unit =
    withOpenedSettingsEditor(fn(lookupListView()))
