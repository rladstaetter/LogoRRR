package app.logorrr.usecases.settings

import app.logorrr.TestFiles
import app.logorrr.conf.Settings
import app.logorrr.usecases.{SingleFileApplicationTest, TestFxBaseApplicationTest}
import app.logorrr.usecases.stg.StgEditorActions
import app.logorrr.views.a11y.uinodes.{LogoRRRMenu, SettingsEditor}
import app.logorrr.views.settings.{SettingsStgListView, TimestampSettingsEditor}
import org.junit.jupiter.api.Test


/**
 * Tests settings dialogue
 */
class SettingsTest extends SingleFileApplicationTest(TestFiles.simpleLog0)
  with StgEditorActions
  with SettingsEditorTestActions:

  def lookupListView(): SettingsStgListView = lookup[SettingsStgListView](SettingsEditor.SettingsStgListView)

  /**
   * -- add a new option to the global settings
   *
   * open (new) file
   * open settings dialog
   * check that there are the default options available
   * click on close button
   *
   * open local settingsgroup editor via button
   * add new group
   * click on heart symbol
   * click on close button
   *
   * open settings dialog
   * check that there is the newly added group in the list
   * click on 'set to factory defaults'
   * check that there is the old list there
   *
   * */
  @Test
  def addANewGroupAndVerifyFactoryDefaults(): Unit =
    val newGroup = "new group"
    addGroup(newGroup)
    addExistingGroupToGlobalGroup(newGroup)

    var found = false
    openSettingsEditorAndPerform(
      settingsListView => {
        settingsListView.getSearchTermGroups.forEach(g => {
          if g.name == newGroup then {
            found = true
          }
        })
      }
    )
    assert(found) // ok, we found an entry

    // check that settings are reset to default
    withOpenedSettingsEditor:
      waitAndClickVisibleItem(SettingsEditor.ResetToDefaultButton)
      assert(lookupListView().getItems.size() == settings.searchTermGroups.size)

  /**
   * Opens settings dialog, checks if the number of shown items for search term groups match the default settings.
   */
  @Test def showEmptySettingsEditor(): Unit =
    openSettingsEditorAndPerform(
      settingsListView => {
        assert(settingsListView.getItems.size() == Settings.Default.searchTermGroups.size)
      }
    )



  private def openSettingsEditorAndPerform(fn: SettingsStgListView => Unit): Unit =
    withOpenedSettingsEditor(fn(lookupListView()))


