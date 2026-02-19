package app.logorrr.usecases.settings

import app.logorrr.TestFiles
import app.logorrr.views.settings.SettingsStgListViewCell
import org.junit.jupiter.api.Test

import scala.jdk.CollectionConverters.*

/**
 * See https://github.com/rladstaetter/LogoRRR/issues/359#issuecomment-3908219522
 * */
class AddCustomFavoriteTest extends ASettingsTest:

  /**
   * - Loads file
   * - clears search terms
   * - adds custom search terms
   * - clicks favorite button
   * - opens settings dialog
   * - checks if there are entries which match custom search terms - expected : some
   * - activates new search terms as default
   * - closes dialog
   * - opens second file
   * - checks if second file contains new default search terms
   */
  @Test
  def cleanSearchTermsAndAddSomeAndClickOnFavoriteAndCheck(): Unit =
    val fileId = files.head
    openFile(fileId)
    clearAllSearchTerms(fileId)
    val searchTerms = Seq("add", "some", "terms")
    search(fileId, searchTerms *)
    clickOnFavoritesButton(fileId)

    // check if we find at least one entry with our custom search terms
    var found = false
    openSettingsEditorAndPerform(
      settingsListView => {
        settingsListView.getSearchTermGroups.forEach(g => {
          found |= (g.termsProperty.asScala.map(_.value) == searchTerms)
        })

        // click on it
        val cells = from(settingsListView).lookup(".list-cell").queryAll[SettingsStgListViewCell].asScala
        cells.find(_.getItem.termsProperty.asScala.map(_.value) == searchTerms) match {
          case Some(value) => clickOn(value)
          case None => ???
        }

      }
    )

    assert(found) // we found the entry and activated it, now open a second file and see if we open a file with new default

    val fileId2 = files.tail.head
    openFile(fileId2)

    // all search terms exist
    assert(searchTerms.forall(s => existsSearchTermToggleButton(fileId2, s)))