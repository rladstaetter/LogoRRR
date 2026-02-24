package app.logorrr.usecases.settings

import app.logorrr.conf.Settings
import org.junit.jupiter.api.Test

class ShowEmptySettingsEditorTest extends ASettingsTest:

  /**
   * Opens settings dialog, checks if the number of shown items for search term groups match the default settings.
   */
  @Test def showEmptySettingsEditor(): Unit =
    openSettingsEditorAndPerform(
      settingsListView => {
        val listViewSize = settingsListView.getItems.size()
        val searchTermGroupsSize = settings.searchTermGroups.size
        assert(listViewSize == searchTermGroupsSize, s"Expected $searchTermGroupsSize but was $listViewSize .")
      }
    )
