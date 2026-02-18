package app.logorrr.usecases.settings

import app.logorrr.TestFiles
import app.logorrr.conf.Settings
import app.logorrr.steps.SearchTermToolbarActions
import app.logorrr.usecases.{MultipleFileApplicationTest, SingleFileApplicationTest, TestFxBaseApplicationTest}
import app.logorrr.usecases.stg.FavoritesActions
import app.logorrr.views.a11y.uinodes.{LogoRRRMenu, SettingsEditor}
import app.logorrr.views.settings.{SettingsStgListView, TimestampSettingsEditor}
import org.junit.jupiter.api.Test

/**
 * Base for tests of settings dialog.
 */
abstract class ASettingsTest extends MultipleFileApplicationTest(TestFiles.seq)
  with FavoritesActions 
  with SearchTermToolbarActions
  with SettingsEditorTestActions 


