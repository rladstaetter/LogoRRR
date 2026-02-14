package app.logorrr.usecases.stg

import app.logorrr.TestFiles
import app.logorrr.conf.Settings
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.a11y.UiNode
import app.logorrr.views.search.st.SearchTermToggleButton
import app.logorrr.views.search.stg.StgChoiceBox
import org.junit.jupiter.api.Test

class StgCheckDefaultTest extends SingleFileApplicationTest(TestFiles.simpleLog0) with StgEditorActions:

  /** traverse all search term groups and select them via the choice box, check visibility */
  @Test def selectDifferentSearchTermGroups(): Unit =
    openFile(fileId)

    // wait for visibility
    waitForVisibility(StgChoiceBox.uiNode(fileId))

    matchItems[String](StgChoiceBox.uiNode(fileId), settings.searchTermGroups.keySet.toSeq.sorted)

    // create a function to use search term choicebox easily
    val selectSearchTermGroup = selectChoiceBoxByValue(StgChoiceBox.uiNode(fileId))

    for ((name, terms) <- settings.searchTermGroups) {
      selectSearchTermGroup(name)
      terms.foreach:
        searchTerm => waitForVisibility(SearchTermToggleButton.uiNode(fileId, searchTerm.value))
    }






