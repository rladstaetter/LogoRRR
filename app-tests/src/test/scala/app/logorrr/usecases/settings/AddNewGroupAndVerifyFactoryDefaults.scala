package app.logorrr.usecases.settings

import app.logorrr.views.a11y.uinodes.SettingsEditor
import org.junit.jupiter.api.Test

class AddNewGroupAndVerifyFactoryDefaults extends ASettingsTest:

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
