package app.logorrr.usecases.settings

import app.logorrr.TestFiles
import app.logorrr.conf.Settings
import app.logorrr.usecases.{SingleFileApplicationTest, TestFxBaseApplicationTest}
import app.logorrr.usecases.stg.StgEditorActions
import app.logorrr.views.a11y.uinodes.{LogoRRRMenu, SettingsEditor}
import app.logorrr.views.settings.{SettingsStgListView, TimestampSettingsEditor}
import org.junit.jupiter.api.Test

/**
 * Base for tests of settings dialog.
 */
abstract class ASettingsTest extends SingleFileApplicationTest(TestFiles.simpleLog0)
  with StgEditorActions with SettingsEditorTestActions


